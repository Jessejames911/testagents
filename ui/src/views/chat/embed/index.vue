<template>
  <div
    class="chat-embed layout-bg"
    v-loading="loading"
    :style="{
      '--el-color-primary': applicationDetail?.custom_theme?.theme_color,
      '--el-color-primary-light-9': hexToRgba(applicationDetail?.custom_theme?.theme_color, 0.1)
    }"
  >
    <div class="chat-embed__header" :style="customStyle">
      <div class="chat-width flex align-center">
        <div class="mr-12 ml-24 flex">
          <AppAvatar
            v-if="isAppIcon(applicationDetail?.icon)"
            shape="square"
            :size="32"
            style="background: none"
          >
            <el-image
              :src="applicationDetail?.icon"
              alt=""
              fit="cover"
              style="width: 32px; height: 32px; display: block"
            />
          </AppAvatar>
          <AppAvatar
            v-else-if="applicationDetail?.name"
            :name="applicationDetail?.name"
            pinyinColor
            shape="square"
            :size="32"
          />
        </div>

        <h4>{{ applicationDetail?.name }}</h4>
      </div>
    </div>
    <div>
      <div class="chat-embed__main">
        <AiChat
          ref="AiChatRef"
          v-model:data="applicationDetail"
          :available="applicationAvailable"
          :appId="applicationDetail?.id"
          :record="currentRecordList"
          :chatId="currentChatId"
          @refresh="refresh"
          @scroll="handleScroll"
          class="AiChat-embed"
        >
          <template #operateBefore>
            <div class="chat-width">
              <el-button type="primary" link class="new-chat-button mb-8" @click="newChat">
                <el-icon><Plus /></el-icon><span class="ml-4">新建对话</span>
              </el-button>
            </div>
          </template>
        </AiChat>
      </div>

      <!-- 历史记录弹出层 -->
      <div
        v-if="applicationDetail.show_history || !user.isEnterprise()"
        @click.prevent.stop="show = !show"
        class="chat-popover-button cursor color-secondary"
      >
        <AppIcon
          iconName="app-history-outlined"
          :style="{
            color: applicationDetail?.custom_theme?.header_font_color
          }"
        ></AppIcon>
      </div>

      <el-collapse-transition>
        <div v-show="show" class="chat-popover w-full" v-click-outside="clickoutside">
          <div class="border-b p-16-24">
            <span>历史记录</span>
          </div>

          <el-scrollbar max-height="300">
            <div class="p-8">
              <common-list
                :style="{ '--el-color-primary': applicationDetail?.custom_theme?.theme_color }"
                :data="chatLogData"
                v-loading="left_loading"
                :defaultActive="currentChatId"
                @click="clickListHandle"
                @mouseenter="mouseenter"
                @mouseleave="mouseId = ''"
              >
                <template #default="{ row }">
                  <div class="flex-between">
                    <auto-tooltip :content="row.abstract">
                      {{ row.abstract }}
                    </auto-tooltip>
                    <div @click.stop v-if="mouseId === row.id && row.id !== 'new'">
                      <el-button style="padding: 0" link @click.stop="deleteLog(row)">
                        <el-icon><Delete /></el-icon>
                      </el-button>
                    </div>
                  </div>
                </template>
                <template #empty>
                  <div class="text-center">
                    <el-text type="info">暂无历史记录</el-text>
                  </div>
                </template>
              </common-list>
            </div>
            <div v-if="chatLogData.length" class="gradient-divider lighter mt-8">
              <span>仅显示最近 20 条对话</span>
            </div>
          </el-scrollbar>
        </div>
      </el-collapse-transition>
      <div class="chat-popover-mask" v-show="show"></div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted, reactive, nextTick, computed } from 'vue'
import { isAppIcon } from '@/utils/application'
import { hexToRgba } from '@/utils/theme'
import useStore from '@/stores'

const { user, log } = useStore()

const AiChatRef = ref()
const loading = ref(false)
const left_loading = ref(false)
const chatLogData = ref<any[]>([])
const show = ref(false)
const props = defineProps<{
  application_profile: any
  applicationAvailable: boolean
}>()
const applicationDetail = computed({
  get: () => {
    return props.application_profile
  },
  set: (v) => {}
})
const paginationConfig = reactive({
  current_page: 1,
  page_size: 20,
  total: 0
})

const currentRecordList = ref<any>([])
const currentChatId = ref('new') // 当前历史记录Id 默认为'new'

const mouseId = ref('')

const customStyle = computed(() => {
  return {
    background: applicationDetail.value?.custom_theme?.theme_color,
    color: applicationDetail.value?.custom_theme?.header_font_color
  }
})

function mouseenter(row: any) {
  mouseId.value = row.id
}
function deleteLog(row: any) {
  log.asyncDelChatClientLog(applicationDetail.value.id, row.id, left_loading).then(() => {
    if (currentChatId.value === row.id) {
      currentChatId.value = 'new'
      paginationConfig.current_page = 1
      paginationConfig.total = 0
      currentRecordList.value = []
    }
    getChatLog(applicationDetail.value.id)
  })
}

function handleScroll(event: any) {
  if (
    currentChatId.value !== 'new' &&
    event.scrollTop === 0 &&
    paginationConfig.total > currentRecordList.value.length
  ) {
    const history_height = event.dialogScrollbar.offsetHeight
    paginationConfig.current_page += 1
    getChatRecord().then(() => {
      event.scrollDiv.setScrollTop(event.dialogScrollbar.offsetHeight - history_height)
    })
  }
}

function clickoutside() {
  show.value = false
}

function newChat() {
  paginationConfig.current_page = 1
  currentRecordList.value = []
  currentChatId.value = 'new'
}

function getChatLog(id: string) {
  const page = {
    current_page: 1,
    page_size: 20
  }

  log.asyncGetChatLogClient(id, page, left_loading).then((res: any) => {
    chatLogData.value = res.data.records
  })
}

function getChatRecord() {
  return log
    .asyncChatRecordLog(
      applicationDetail.value.id,
      currentChatId.value,
      paginationConfig,
      loading,
      false
    )
    .then((res: any) => {
      paginationConfig.total = res.data.total
      const list = res.data.records
      list.map((v: any) => {
        v['write_ed'] = true
        v['record_id'] = v.id
      })
      currentRecordList.value = [...list, ...currentRecordList.value].sort((a, b) =>
        a.create_time.localeCompare(b.create_time)
      )
      if (paginationConfig.current_page === 1) {
        nextTick(() => {
          // 将滚动条滚动到最下面
          AiChatRef.value.setScrollBottom()
        })
      }
    })
}

const clickListHandle = (item: any) => {
  if (item.id !== currentChatId.value) {
    paginationConfig.current_page = 1
    currentRecordList.value = []
    currentChatId.value = item.id
    if (currentChatId.value !== 'new') {
      getChatRecord()
    }
    show.value = false
  }
}

function refresh(id: string) {
  getChatLog(applicationDetail.value.id)
  currentChatId.value = id
}
/**
 *初始化历史对话记录
 */
const init = () => {
  if (applicationDetail.value.show_history || !user.isEnterprise()) {
    getChatLog(applicationDetail.value.id)
  }
}

onMounted(() => {
  init()
})
</script>
<style lang="scss">
.chat-embed {
  overflow: hidden;
  &__header {
    background: var(--app-header-bg-color);
    position: fixed;
    width: 100%;
    left: 0;
    top: 0;
    z-index: 100;
    height: var(--app-header-height);
    line-height: var(--app-header-height);
    box-sizing: border-box;
    border-bottom: 1px solid var(--el-border-color);
  }
  &__main {
    padding-top: calc(var(--app-header-height) + 24px);
    height: calc(100vh - var(--app-header-height) - 24px);
    overflow: hidden;
  }
  .new-chat-button {
    z-index: 11;
  }
  // 历史对话弹出层
  .chat-popover {
    position: absolute;
    top: var(--app-header-height);
    background: #ffffff;
    padding-bottom: 24px;
    z-index: 2009;
  }
  .chat-popover-button {
    z-index: 2009;
    position: absolute;
    top: 16px;
    right: 85px;
    font-size: 22px;
  }
  .chat-popover-mask {
    background-color: var(--el-overlay-color-lighter);
    bottom: 0;
    height: 100%;
    left: 0;
    overflow: auto;
    position: fixed;
    right: 0;
    top: var(--app-header-height);
    z-index: 2008;
  }
  .gradient-divider {
    position: relative;
    text-align: center;
    color: var(--el-color-info);
    ::before {
      content: '';
      width: 17%;
      height: 1px;
      background: linear-gradient(90deg, rgba(222, 224, 227, 0) 0%, #dee0e3 100%);
      position: absolute;
      left: 16px;
      top: 50%;
    }
    ::after {
      content: '';
      width: 17%;
      height: 1px;
      background: linear-gradient(90deg, #dee0e3 0%, rgba(222, 224, 227, 0) 100%);
      position: absolute;
      right: 16px;
      top: 50%;
    }
  }
  .AiChat-embed {
    .ai-chat__operate {
      padding-top: 12px;
    }
  }
  .chat-width {
    max-width: var(--app-chat-width, 860px);
    margin: 0 auto;
  }
}
</style>
<style lang="scss" scoped>
:deep(.el-overlay) {
  background-color: transparent;
}
</style>
