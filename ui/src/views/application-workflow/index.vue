<template>
  <div class="application-workflow" v-loading="loading">
    <div class="header border-b flex-between p-12-24">
      <div class="flex align-center">
        <back-button
          @click="router.push({ path: `/application/${id}/WORK_FLOW/overview` })"
        ></back-button>
        <h4>{{ detail?.name }}</h4>
        <div v-if="showHistory && disablePublic">
          <el-text type="info" class="ml-16 color-secondary"
            >预览版本：
            {{ currentVersion.name || datetimeFormat(currentVersion.update_time) }}</el-text
          >
        </div>
        <el-text type="info" class="ml-16 color-secondary" v-else-if="saveTime"
          >保存时间：{{ datetimeFormat(saveTime) }}</el-text
        >
      </div>
      <div v-if="showHistory && disablePublic">
        <el-button type="primary" class="mr-8" @click="refreshVersion()"> 恢复版本 </el-button>
        <el-divider direction="vertical" />
        <el-button text @click="closeHistory">
          <el-icon><Close /></el-icon>
        </el-button>
      </div>
      <div v-else>
        <el-button icon="Plus" @click="showPopover = !showPopover"> 添加组件 </el-button>
        <el-button @click="clickShowDebug" :disabled="showDebug">
          <AppIcon iconName="app-play-outlined" class="mr-4"></AppIcon>
          调试</el-button
        >
        <el-button @click="saveApplication(true)">
          <AppIcon iconName="app-save-outlined" class="mr-4"></AppIcon>
          保存
        </el-button>
        <el-button type="primary" @click="publicHandle"> 发布 </el-button>

        <el-dropdown trigger="click">
          <el-button text @click.stop class="ml-8 mt-4">
            <el-icon class="rotate-90"><MoreFilled /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="openHistory">
                <AppIcon iconName="app-history-outlined"></AppIcon>
                发布历史
              </el-dropdown-item>
              <el-dropdown-item>
                <AppIcon iconName="app-save-outlined"></AppIcon>
                自动保存
                <div class="ml-4">
                  <el-switch size="small" v-model="isSave" @change="changeSave" />
                </div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
    <!-- 下拉框 -->
    <el-collapse-transition>
      <DropdownMenu
        :show="showPopover"
        :id="id"
        v-click-outside="clickoutside"
        @clickNodes="clickNodes"
        @onmousedown="onmousedown"
        :workflowRef="workflowRef"
      />
    </el-collapse-transition>
    <!-- 主画布 -->
    <div class="workflow-main" ref="workflowMainRef">
      <workflow ref="workflowRef" v-if="detail" :data="detail?.work_flow" />
    </div>
    <!-- 调试 -->
    <el-collapse-transition>
      <div
        v-click-outside="clickoutsideDebug"
        class="workflow-debug-container"
        :class="enlarge ? 'enlarge' : ''"
        v-if="showDebug"
      >
        <div class="workflow-debug-header" :class="!isDefaultTheme ? 'custom-header' : ''">
          <div class="flex-between">
            <div class="flex align-center">
              <div class="mr-12 ml-24 flex">
                <AppAvatar
                  v-if="isAppIcon(detail?.icon)"
                  shape="square"
                  :size="32"
                  style="background: none"
                >
                  <el-image
                    :src="detail?.icon"
                    alt=""
                    fit="cover"
                    style="width: 32px; height: 32px; display: block"
                  />
                </AppAvatar>
                <AppAvatar
                  v-else-if="detail?.name"
                  :name="detail?.name"
                  pinyinColor
                  shape="square"
                  :size="32"
                />
              </div>

              <h4>
                {{ detail?.name || $t('views.application.applicationForm.form.appName.label') }}
              </h4>
            </div>
            <div class="mr-16">
              <el-button link @click="enlarge = !enlarge">
                <AppIcon
                  :iconName="enlarge ? 'app-minify' : 'app-magnify'"
                  class="color-secondary"
                  style="font-size: 20px"
                ></AppIcon>
              </el-button>
              <el-button link @click="showDebug = false">
                <el-icon :size="20" class="color-secondary"><Close /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
        <div class="scrollbar-height">
          <AiChat :data="detail" :debug="true"></AiChat>
        </div>
      </div>
    </el-collapse-transition>
    <!-- 发布历史 -->
    <PublishHistory
      v-if="showHistory"
      @click="checkVersion"
      v-click-outside="clickoutsideHistory"
      @refreshVersion="refreshVersion"
    />
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, computed, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Workflow from '@/workflow/index.vue'
import DropdownMenu from '@/views/application-workflow/component/DropdownMenu.vue'
import PublishHistory from '@/views/application-workflow/component/PublishHistory.vue'
import applicationApi from '@/api/application'
import { isAppIcon } from '@/utils/application'
import { MsgSuccess, MsgError } from '@/utils/message'
import { datetimeFormat } from '@/utils/time'
import useStore from '@/stores'
import { WorkFlowInstance } from '@/workflow/common/validate'
import { hasPermission } from '@/utils/permission'

const { user, application } = useStore()
const router = useRouter()
const route = useRoute()

const isDefaultTheme = computed(() => {
  return user.isDefaultTheme()
})
const {
  params: { id }
} = route as any

let interval: any
const workflowRef = ref()
const workflowMainRef = ref()
const loading = ref(false)
const detail = ref<any>(null)

const showPopover = ref(false)
const showDebug = ref(false)
const enlarge = ref(false)
const saveTime = ref<any>('')
const isSave = ref(false)
const showHistory = ref(false)
const disablePublic = ref(false)
const currentVersion = ref<any>({})

function clickoutsideHistory() {
  if (!disablePublic.value) {
    showHistory.value = false
    disablePublic.value = false
  }
}

function refreshVersion(item?: any) {
  if (item) {
    renderGraphData(item)
  }
  if (hasPermission(`APPLICATION:MANAGE:${id}`, 'AND') && isSave.value) {
    initInterval()
  }
  showHistory.value = false
  disablePublic.value = false
}

function checkVersion(item: any) {
  disablePublic.value = true
  currentVersion.value = item
  renderGraphData(item)
  closeInterval()
}

function renderGraphData(item: any) {
  item.work_flow['nodes'].map((v: any) => {
    v['properties']['noRender'] = true
  })
  detail.value.work_flow = item.work_flow
  saveTime.value = item?.update_time
  workflowRef.value?.clearGraphData()
  nextTick(() => {
    workflowRef.value?.render(item.work_flow)
  })
}

function closeHistory() {
  getDetail()
  if (hasPermission(`APPLICATION:MANAGE:${id}`, 'AND') && isSave.value) {
    initInterval()
  }
  showHistory.value = false
  disablePublic.value = false
}

function openHistory() {
  showHistory.value = true
}

function changeSave(bool: boolean) {
  bool ? initInterval() : closeInterval()
  localStorage.setItem('workflowAutoSave', bool.toString())
}

function clickNodes(item: any) {
  // workflowRef.value?.addNode(item)
  showPopover.value = false
}

function onmousedown(item: any) {
  // workflowRef.value?.onmousedown(item)
  showPopover.value = false
}

function clickoutside() {
  showPopover.value = false
}
function publicHandle() {
  workflowRef.value
    ?.validate()
    .then(() => {
      const obj = {
        work_flow: getGraphData()
      }
      const workflow = new WorkFlowInstance(obj.work_flow)
      try {
        workflow.is_valid()
      } catch (e: any) {
        MsgError(e.toString())
        return
      }
      applicationApi.putPublishApplication(id as String, obj, loading).then(() => {
        MsgSuccess('发布成功')
      })
    })
    .catch((res: any) => {
      const node = res.node
      const err_message = res.errMessage
      if (typeof err_message == 'string') {
        MsgError(res.node.properties?.stepName + '节点 ' + err_message)
      } else {
        const keys = Object.keys(err_message)
        MsgError(node.properties?.stepName + '节点 ' + err_message[keys[0]]?.[0]?.message)
      }
    })
}

const clickShowDebug = () => {
  workflowRef.value
    ?.validate()
    .then(() => {
      const graphData = getGraphData()
      const workflow = new WorkFlowInstance(graphData)
      try {
        workflow.is_valid()
        detail.value = {
          ...detail.value,
          type: 'WORK_FLOW',
          ...workflow.get_base_node()?.properties.node_data,
          work_flow: getGraphData()
        }

        showDebug.value = true
      } catch (e: any) {
        MsgError(e.toString())
      }
    })
    .catch((res: any) => {
      const node = res.node
      const err_message = res.errMessage
      if (typeof err_message == 'string') {
        MsgError(res.node.properties?.stepName + '节点 ' + err_message)
      } else {
        const keys = Object.keys(err_message)
        MsgError(node.properties?.stepName + '节点 ' + err_message[keys[0]]?.[0]?.message)
      }
    })
}
function clickoutsideDebug(e: any) {
  if (workflowMainRef.value && e && e.target && workflowMainRef.value.contains(e?.target)) {
    showDebug.value = false
  }
}

function getGraphData() {
  return workflowRef.value?.getGraphData()
}

function getDetail() {
  application.asyncGetApplicationDetail(id).then((res: any) => {
    res.data?.work_flow['nodes'].map((v: any) => {
      v['properties']['noRender'] = true
    })
    detail.value = res.data
    detail.value.stt_model_id = res.data.stt_model
    detail.value.tts_model_id = res.data.tts_model
    detail.value.tts_type = res.data.tts_type
    saveTime.value = res.data?.update_time
    workflowRef.value?.clearGraphData()
    nextTick(() => {
      workflowRef.value?.renderGraphData(detail.value.work_flow)
    })
  })
}

function saveApplication(bool?: boolean) {
  const obj = {
    work_flow: getGraphData()
  }
  application.asyncPutApplication(id, obj).then((res) => {
    saveTime.value = new Date()
    if (bool) {
      MsgSuccess('保存成功')
    }
  })
}

/**
 * 定时保存
 */
const initInterval = () => {
  interval = setInterval(() => {
    saveApplication()
  }, 60000)
}

/**
 * 关闭定时
 */
const closeInterval = () => {
  if (interval) {
    clearInterval(interval)
  }
}

onMounted(() => {
  getDetail()
  const workflowAutoSave = localStorage.getItem('workflowAutoSave')
  isSave.value = workflowAutoSave === 'true' ? true : false
  // 初始化定时任务
  if (hasPermission(`APPLICATION:MANAGE:${id}`, 'AND') && isSave.value) {
    initInterval()
  }
})

onBeforeUnmount(() => {
  // 清除定时任务
  closeInterval()
  workflowRef.value?.clearGraphData()
})
</script>
<style lang="scss">
.application-workflow {
  background: var(--app-layout-bg-color);
  height: 100%;
  .header {
    background: #ffffff;
  }
  .workflow-main {
    height: calc(100vh - 62px);
    box-sizing: border-box;
  }

  .workflow-dropdown-tabs {
    .el-tabs__nav-wrap {
      padding: 0 16px;
    }
  }
}

.workflow-debug-container {
  z-index: 2000;
  position: relative;
  border-radius: 8px;
  border: 1px solid #ffffff;
  background: linear-gradient(
      188deg,
      rgba(235, 241, 255, 0.2) 39.6%,
      rgba(231, 249, 255, 0.2) 94.3%
    ),
    #eff0f1;
  box-shadow: 0px 4px 8px 0px rgba(31, 35, 41, 0.1);
  position: fixed;
  bottom: 16px;
  right: 16px;
  overflow: hidden;
  width: 450px;
  height: 600px;
  .workflow-debug-header {
    background: var(--app-header-bg-color);
    height: var(--app-header-height);
    line-height: var(--app-header-height);
    box-sizing: border-box;
    border-bottom: 1px solid var(--el-border-color);
  }
  .scrollbar-height {
    height: calc(100% - var(--app-header-height) - 24px);
    padding-top: 24px;
  }
  &.enlarge {
    width: 50% !important;
    height: 100% !important;
    bottom: 0 !important;
    right: 0 !important;
  }
  .chat-width {
    max-width: 100% !important;
    margin: 0 auto;
  }
}
</style>
