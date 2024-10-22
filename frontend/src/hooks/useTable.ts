import { h } from 'vue'
import { useStore } from 'vuex'

export interface TableHeaders {
  [key: string]: string
}
export type TableData = {
  [K in keyof TableHeaders]: string | number
}

const useTable = () => {
  const store = useStore()
  return (tableHeaders: TableHeaders, data: TableData[]) => {
    store.commit('setModal', {
      isOpen: true,
      content: h('table', { class: 'min-w-full border-collapse border border-gray-200' }, [
        // 테이블 헤더
        h('thead', {}, [
          h('tr', { class: 'bg-gray-200' }, Object.values(tableHeaders).map(header =>
            h('th', { class: 'px-4 py-2 border border-gray-300 text-left text-gray-600 font-medium' }, header)
          ))
        ]),
        // 테이블 바디
        h('tbody', {}, data.map((row: any, index: number) =>
          h('tr', { class: index % 2 === 0 ? 'bg-white' : 'bg-gray-50' }, Object.keys(tableHeaders).map(key =>
            h('td', { class: 'px-4 py-2 border border-gray-300 text-gray-700' }, row[key])
          ))
        ))
      ])
    })
  }
}
export default useTable
