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
  modalType: ModalType
  selectBoxValue: SelectOption
}



export const selectOptions = ['forest', 'red', 'black', 'skyblue','orange','purple'] as const
export type SelectOption = ElementType<typeof selectOptions>
export type InputValue = string
export type OnConfirm = (() => void) | ((inputValue: InputValue) => void) | null
export type OnClose = (() => void) | null

export const modalTypes = ['alert','input'] as const

export type ModalType = ElementType<typeof modalTypes>


const modalModule: Module<ModalModuleState, VuexModules> = {
  state() {
    return {
      isOpen: false,
      content: null,
      onClose: null,
      onConfirm: null,
      inputValue: '',
      placeholder: '',
      modalType: 'alert', // default
      selectBoxValue: 'green'
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
      inputValue,
      selectBoxValue
    }: ModalModuleState) {
      state.content = content ?? null
      state.isOpen = isOpen ?? false
      state.onClose = onClose ?? null
      state.onConfirm = onConfirm ?? null
      state.modalType = modalType ?? 'alert'
      state.placeholder = placeholder ?? ''
      state.inputValue = inputValue ?? ''
      state.selectBoxValue = selectBoxValue ?? 'green'
    },
    setInputValue(state, payload) {
      state.inputValue = payload
    },
    setSelectBoxValue(state, payload) {
      state.selectBoxValue = payload
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
        } else if(state.modalType === 'alert'){
          state.onConfirm?.()
        }
      }
    },
    getSelectBoxValue(state) {
      return state.selectBoxValue
    }
  }
}

export default modalModule
