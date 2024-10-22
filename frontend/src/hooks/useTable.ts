import { h } from 'vue'
import { useStore } from 'vuex'
import { ModalModuleState } from '../stores/vuex/modules/modal.ts'

export type TableHeaders = Record<string, string>

export type TableData<T extends TableHeaders> = {
  [K in keyof T]: string | number
}

export type TableCellCallback<T extends TableHeaders> = {
  [K in keyof T]?: (rowData: TableData<T>, cellValue: string | number) => void
}

export type TableRowCallback<T extends TableHeaders> = (rowData: TableData<T>) => void

export interface TableOptions<T extends TableHeaders> {
  onCellClick?: TableCellCallback<T>
  onRowClick?: TableRowCallback<T>
  additionalPayload?: Partial<ModalModuleState>
}

const useTable = () => {
  const store = useStore()
  return <T extends TableHeaders> (tableHeaders: T, data: TableData<T>[], options?: TableOptions<T>) => {
    const { onCellClick, onRowClick, additionalPayload } = options ?? {}
    store.commit('setModal', {
      isOpen: true,
      ...additionalPayload,
      content: h('table', { class: 'min-w-full border-collapse border border-gray-200' }, [
        // 테이블 헤더
        h('thead', {}, [
          h('tr', { class: 'bg-gray-200' }, Object.values(tableHeaders).map(header =>
            h('th', { class: 'px-4 py-2 border border-gray-300 text-left text-gray-600 font-medium' }, header)
          ))
        ]),
        // 테이블 바디
        h('tbody', {}, data.map((row: TableData<T>, index: number) =>
          h('tr', {
            class: (index % 2 === 0 ? 'bg-white' : 'bg-gray-50') + (onRowClick && ' hover:font-bold cursor-pointer'),
            onClick: () => onRowClick?.(row)
          }, Object.keys(tableHeaders).map(key => {
              return h('td', {
                class: 'px-4 py-2 border border-gray-300 text-gray-700' + (onCellClick?.[key] && ' hover:font-bold cursor-pointer'),
                onClick: (e) => {
                  if (onCellClick?.[key]) { // cell click 이벤트있다면 row click 이벤트 실행시키지 않음
                    e.stopPropagation()
                  }
                  onCellClick?.[key]?.(row, row[key])
                }
              }, row[key])
            }
          ))
        ))
      ])
    })
  }
}
export default useTable
