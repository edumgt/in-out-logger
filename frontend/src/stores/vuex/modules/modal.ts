import { Module } from 'vuex'
import { VuexModules } from '../index.ts'
import { VNode } from 'vue'


export interface ModalModuleState {
  isOpen: boolean
  content: (() => VNode) | null
  onClose: OnClose
  onConfirm: OnConfirm
  placeholder: string
  inputValue: InputValue
  modalType: ModalTypes
}

export type InputValue = string
export type OnConfirm = (() => void) | ((inputValue: InputValue) => void) | null
export type OnClose = (() => void) | null

export const modalTypes = {
  alert: '알림',
  input: '입력'
} as const

export type ModalTypes = keyof typeof modalTypes;


const modalModule: Module<ModalModuleState, VuexModules> = {
  state() {
    return {
      isOpen: false,
      content: null,
      onClose: null,
      onConfirm: null,
      inputValue: '',
      placeholder: '',
      modalType: 'alert'
    }
  },
  actions: {},
  mutations: {
    setModal(state, {
      isOpen,
      content,
      onClose,
      onConfirm,
      modalType,
      placeholder,
      inputValue
    }: ModalModuleState) {
      state.isOpen = isOpen ?? state.isOpen
      state.content = content ?? state.content
      state.onClose = onClose ?? state.onClose
      state.onConfirm = onConfirm ?? state.onConfirm
      state.modalType = modalType ?? 'alert'
      state.placeholder = placeholder ?? ''
      state.inputValue = inputValue ?? ''
    },
    setInputValue(state, payload) {
      state.inputValue = payload
    }
  },
  getters: {
    isAlertModalOpen(state) {
      return state.isOpen && state.modalType === 'alert'
    },
    isInputModalOpen(state) {
      return state.isOpen && state.modalType === 'input'
    },
    getContent(state) {
      return state.content
    },
    getInputValue(state) {
      return state.inputValue
    },
    getPlaceholder(state) {
      return state.placeholder
    },
    handleModalClose(state) {
      return () => {
        state.isOpen = false
        state.onClose?.()
      }
    },
    handleModalConfirm(state) {
      return () => {
        state.isOpen = false
        if (state.modalType === 'input') {
          state.onConfirm?.(state.inputValue)
        } else {
          state.onConfirm?.()
        }
      }
    }
  }
}

export default modalModule
