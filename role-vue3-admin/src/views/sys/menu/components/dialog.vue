<template>
  <el-dialog
      :model-value="dialogVisible"
      :title="dialogTitle"
      width="30%"
      @close="handleClose"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="上级菜单" prop="parentId">
        <el-select v-model="form.parentId" placeholder="请选择上级菜单">
          <template v-for="item in tableData" :key="item.id">
            <el-option :label="item.name" :value="item.id"></el-option>
            <template v-for="child in item.children" :key="child.id">
              <el-option :label="child.name" :value="child.id">
                <span>{{ "    -- " + child.name }}</span>
              </el-option>
            </template>
          </template>
        </el-select>
      </el-form-item>

      <el-form-item label="菜单类型" prop="menuType">
        <el-radio-group v-model="form.menuType">
          <el-radio label="M">目录</el-radio>
          <el-radio label="C">菜单</el-radio>
          <el-radio label="F">按钮</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="菜单图标" prop="icon">
        <el-input v-model="form.icon" />
      </el-form-item>

      <el-form-item label="菜单名称" prop="name">
        <el-input v-model="form.name" />
      </el-form-item>

      <el-form-item label="权限标识" prop="perms">
        <el-input v-model="form.perms" />
      </el-form-item>

      <el-form-item label="组件路径" prop="component">
        <el-input v-model="form.component" />
      </el-form-item>

      <el-form-item label="显示顺序" prop="orderNum">
        <el-input-number v-model="form.orderNum" :min="1" label="显示顺序"></el-input-number>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="handleConfirm">确认</el-button>
        <el-button @click="handleClose">取消</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { defineEmits, defineProps, ref, watch } from 'vue';
import requestUtil from '@/util/request';
import { ElMessage } from 'element-plus';

const props = defineProps({
  id: {
    type: Number,
    default: -1,
    required: true,
  },
  dialogTitle: {
    type: String,
    default: '',
    required: true,
  },
  dialogVisible: {
    type: Boolean,
    default: false,
    required: true,
  },
  tableData: {
    type: Array,
    default: () => [],
    required: true,
  },
});

const form = ref({
  id: -1,
  parentId: '',
  menuType: 'M',
  icon: '',
  name: '',
  perms: '',
  component: '',
  orderNum: 1,
});

const rules = ref({
  parentId: [{ required: true, message: '请选择上级菜单' }],
  name: [{ required: true, message: '菜单名称不能为空', trigger: 'blur' }],
});

const formRef = ref(null);

// 初始化表单数据
const initFormData = async (id) => {
  const res = await requestUtil.get('sys/menu/' + id);
  form.value = res.data.sysMenu;
};

// 监听对话框状态
watch(
    () => props.dialogVisible,
    () => {
      if (props.id !== -1) {
        initFormData(props.id);
      } else {
        form.value = {
          id: -1,
          parentId: '',
          menuType: 'M',
          icon: '',
          name: '',
          perms: '',
          component: '',
          orderNum: 1,
        };
      }
    }
);

const emits = defineEmits(['update:modelValue', 'initMenuList']);

// 关闭对话框
const handleClose = () => {
  emits('update:modelValue', false);
};

// 确认提交
const handleConfirm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      const result = await requestUtil.post('sys/menu/save', form.value);
      if (result.data.code === 200) {
        ElMessage.success('执行成功！');
        emits('initMenuList');
        handleClose();
      } else {
        ElMessage.error(result.data.msg);
      }
    }
  });
};
</script>

<style scoped></style>