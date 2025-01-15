<template>
  <el-dialog
      :model-value="dialogVisible"
      :title="dialogTitle"
      width="30%"
      @update:modelValue="handleClose"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="角色名称" prop="name">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="权限字符" prop="code">
        <el-input v-model="form.code" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="4" />
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
  dialogVisible: Boolean,
  dialogTitle: String,
  id: Number,
});

const emits = defineEmits(['update:modelValue', 'initRoleList']);

const form = ref({
  id: -1,
  name: '',
  code: '',
  remark: '',
});

const rules = ref({
  name: [{ required: true, message: '请输入角色名称' }],
  code: [{ required: true, message: '请输入权限字符' }],
});

const formRef = ref(null);

// 初始化表单数据
const initFormData = async (id) => {
  const res = await requestUtil.get('sys/role/' + id);
  form.value = res.data.sysRole;
};

watch(
    () => props.dialogVisible,
    () => {
      if (props.id !== -1) {
        initFormData(props.id);
      } else {
        form.value = { id: -1, name: '', code: '', remark: '' };
      }
    }
);

// 关闭对话框
const handleClose = () => {
  emits('update:modelValue', false);
};

// 确认提交
const handleConfirm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      const result = await requestUtil.post('sys/role/save', form.value);
      if (result.data.code === 200) {
        ElMessage.success('执行成功！');
        formRef.value.resetFields();
        emits('initRoleList');
        handleClose();
      } else {
        ElMessage.error(result.data.msg);
      }
    }
  });
};
</script>