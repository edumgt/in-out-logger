<script setup lang="ts">
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import { computed, h, nextTick, onMounted, onUnmounted, ref } from 'vue'
import axios from '@/utils/axios.ts'
import { useProgress } from '@/utils/etc.ts'
import { useStore } from 'vuex'
import { InputValue, SelectOption, selectOptions } from '@/stores/vuex/modules/modal.ts'
import { CalendarOptions, DateSelectArg, EventApi, EventClickArg } from '@fullcalendar/core'
import { dateToNumber } from '@/utils/string.ts'
import { isAxiosError } from 'axios'
import { messageHandler } from '@/utils/error.ts'

type Direction = 'asc' | 'desc'

interface FetchedYears {
  [key: number]: boolean;
}

const calendarRef = ref<InstanceType<typeof FullCalendar>>()
const calendarApi = computed(() => (calendarRef.value as InstanceType<typeof FullCalendar>).getApi())
const currentEvents = ref<EventApi[]>([])
const fetchedYears = ref<FetchedYears>({})

const handleWeekendsToggle = () => {
  calendarOptions.value.weekends = !calendarOptions.value.weekends // update a property
}
const selectBoxValue = computed(() => store.getters.getSelectBoxValue)

const handleDateSelect = (selectInfo: DateSelectArg) => {
  store.commit('setModal', {
    isOpen: true,
    modalType: 'input',
    content: h('div', { 'class': 'flex justify-between' }, [
      h('p', '일정명을 입력해주세요.'),
      h('div', { 'class': `flex gap-1` }, [
        h('p', {'class': 'mt-2 mr-1'},'배경색 지정'),
        h('select', {
            onChange(event: any) {
              store.commit('setSelectBoxValue', event.target.value)
            }
          },
          selectOptions.map((option: SelectOption) =>
            h('option', { value: option }, option)
          )
        )
      ])
    ]),
    placeholder: '일정명',
    options: {
      propagation: true
    },
    onConfirm: async (inputValue: InputValue) => {
      let calendarApi = selectInfo.view.calendar
      calendarApi.unselect() // clear date selection

      if (!inputValue) {
        return
      }
      const response = await axios.post('/api/calendar/events', {
        title: `${inputValue} (${username.value})`,
        start: selectInfo.startStr,
        end: selectInfo.endStr,
        backgroundColor: selectBoxValue.value
      })
      const { id, start, end, title } = response.data
      calendarApi.addEvent({
        id,
        title,
        start,
        end,
        allDay: true,
        backgroundColor: selectBoxValue.value,
      })
    }
  })

}

const handleEventClick = (clickInfo: EventClickArg) => {
  store.commit('setModal', {
    isOpen: true,
    content: h('p', '일정을 삭제할까요?'),
    onConfirm: async () => {
      try {
        await axios.delete(`/api/calendar/events/${clickInfo.event.id}`)
        clickInfo.event.remove()
      } catch(e:any){
        if(isAxiosError(e)){
          store.commit('setModal', {
            isOpen: true,
            content: h('p', messageHandler(e)),
          })
        }
      }
    }
  })
}

const direction = ref<Direction>('asc')

const handleClickCheckbox = () => {
  const opposite: Direction = direction.value === 'asc' ? 'desc' : 'asc'
  direction.value = opposite
  sortEvents()
}

const sortEvents = () => {
  if (direction.value === 'asc') {
    currentEvents.value.sort((a, b) => dateToNumber(a.startStr) - dateToNumber(b.startStr))
  } else {
    currentEvents.value.sort((a, b) => dateToNumber(b.startStr) - dateToNumber(a.startStr))
  }
}


const handleEvents = (events: EventApi[]) => {
  currentEvents.value = events
  sortEvents()
  console.log('set', events[0]?.startStr)
}
const calendarOptions = ref<CalendarOptions>({
  plugins: [
    dayGridPlugin,
    timeGridPlugin,
    interactionPlugin // dateClick
  ],
  headerToolbar: {
    left: 'prevYear prev',
    center: 'title',
    right: 'next nextYear'
  },
  locale: 'kr',
  initialView: 'dayGridMonth',
  initialDate: new Date(),
  editable: true,
  selectable: true,
  selectMirror: true,
  dayMaxEvents: true,
  weekends: true,
  select: handleDateSelect,
  eventClick: handleEventClick,
  eventsSet: handleEvents,
  async datesSet(arg) {
    store.commit('setIsLoading', true)
    const { start, end } = arg
    const startTime = start.getTime()
    const endTime = end.getTime()

    // 평균 타임스탬프 계산
    const averageTime = (startTime + endTime) / 2
    const currentDate = new Date(averageTime)
    const currentYear = currentDate.getFullYear()
    if (currentYear < 2000) {
      store.commit('setModal', {
        isOpen: true,
        content: h('p', '2000년 이전 날짜는 조회 불가능합니다.'),
        onClose: () => calendarApi.value.gotoDate(new Date()),
        onConfirm: () => calendarApi.value.gotoDate(new Date())
      })
      store.commit('setIsLoading', false)
      return
    }
    const events = await fetchEvents(currentYear)
    if(events){
      for (const { id, title, start, end, backgroundColor } of events) {
        calendarApi.value.addEvent({
          id,
          title,
          start,
          end,
          allDay: true,
          backgroundColor
        })
      }
    }
    store.commit('setIsLoading', false)
  }
  /* you can update a remote database when these fire:
  eventAdd:
  eventChange:
  eventRemove:
  */
})
const fetchEvents = async (year: number) => {
  if(fetchedYears.value[year]){
    return
  }
  console.log('data fetching...')
  const response = await axios.get(`/api/calendar/events/${year}`)
  fetchedYears.value[year] = true
  console.log('res', response.data)
  return response.data
}


onMounted(async () => {
  await nextTick()
  for (let i = 2000; i < 2100; i++) {
    fetchedYears.value[i] = false
  }
})

onUnmounted(() => {
  store.commit('setModal', {
    isOpen: false
  })
})

const handleCheckIn = async () => {
  try {
    await axios.post('/api/commutes')
  } catch (e: any) {
    store.commit('setModal', {
      isOpen: true,
      content: h('p', messageHandler(e)),
    })
    throw e
  }
}
const handleCheckOut = async () => {
  try {
    const response = await axios.patch('/api/commutes')
    store.commit('setModal', {
      isOpen: true,
      content: h('p', response.data),
    })
  } catch (e: any) {
    store.commit('setModal', {
      isOpen: true,
      content: h('p', messageHandler(e))
    })
    throw e
  }
}
const checkInProgress = useProgress(handleCheckIn)
const checkOutProgress = useProgress(handleCheckOut)

const store = useStore()
const username = computed(() => store.getters.getUsername)

const handleNavigateCalendar = (eventStartDate: string) => {
  calendarApi.value.gotoDate(eventStartDate)
}

</script>

<template>
  <div class='main-wrap'>
    <aside class="top-0 left-0 z-40 w-64 h-screen" aria-label="Sidebar">
      <div class="h-full px-3 py-4 overflow-y-auto bg-gray-50 dark:bg-gray-800">
        <ul class="space-y-2 font-medium">
          <li>
            <router-link to="/" class="flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24">
                <path fill="currentColor" d="m12 5.69l5 4.5V18h-2v-6H9v6H7v-7.81zM12 3L2 12h3v8h6v-6h2v6h6v-8h3z" />
              </svg>
              <span class="ms-3">메인으로</span>
            </router-link>
          </li>
          <li>
            <div @click="checkInProgress" class="cursor-pointer flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><g fill="currentColor"><path d="M14 19a1 1 0 1 0 0 2h5a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2h-5a1 1 0 1 0 0 2h5v14z"/><path d="M15.714 12.7a1 1 0 0 0 .286-.697v-.006a1 1 0 0 0-.293-.704l-4-4a1 1 0 1 0-1.414 1.414L12.586 11H3a1 1 0 1 0 0 2h9.586l-2.293 2.293a1 1 0 1 0 1.414 1.414l4-4z"/></g></svg>
              <span class="flex-1 ms-3 whitespace-nowrap">출근</span>
            </div>
          </li>
          <li>
            <div @click="checkOutProgress" class="cursor-pointer flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><g fill="currentColor"><path fill-rule="evenodd" d="M11 20a1 1 0 0 0-1-1H5V5h5a1 1 0 1 0 0-2H5a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h5a1 1 0 0 0 1-1" clip-rule="evenodd"/><path d="M21.714 12.7a1 1 0 0 0 .286-.697v-.006a1 1 0 0 0-.293-.704l-4-4a1 1 0 1 0-1.414 1.414L18.586 11H9a1 1 0 1 0 0 2h9.586l-2.293 2.293a1 1 0 0 0 1.414 1.414l4-4z"/></g></svg>
              <span class="flex-1 ms-3 whitespace-nowrap">퇴근</span>
            </div>
          </li>
          <li>
            <div @click="() => {
              store.commit('setModal',{
                isOpen: true,
                content: h('p','개발중입니다.')
              })
            }" class="cursor-pointer flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path fill="currentColor" d="M18 2a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2zm0 2h-5v8l-2.5-2.25L8 12V4H6v16h12z"/></svg>
              <span class="flex-1 ms-3 whitespace-nowrap">출근부</span> <!-- TODO 출근부 -->
            </div>
          </li>
          <li class='sidebar-section'>
            <label> <input type='checkbox' :checked='calendarOptions.weekends' @change='handleWeekendsToggle' /> toggle
              weekends </label>
          </li>
          <li class='sidebar-section w-full'>
            <h2>All Events ({{ currentEvents.length }})</h2>
            <div class="mt-1 flex gap-1">
              <input @change="handleClickCheckbox" :checked="direction === 'asc'" id="direction" type="checkbox" />
              <label for="direction">오름차순</label>
            </div>
            <ul>
              <li v-for='event in currentEvents' :key='event.id'>
                <div class="cursor-pointer" @click="() => handleNavigateCalendar(event.startStr)">
                  <b>{{ event.startStr }}</b> <i>{{ event.title }}</i>
                </div>
              </li>
            </ul>
          </li>
        </ul>
      </div>
    </aside>

    <div class='calendar-wrap'>
      <FullCalendar class='calendar-app' ref="calendarRef" :options='calendarOptions'>
        <template v-slot:eventContent='arg'>
          <b>{{ arg.timeText }}</b> <i>{{ arg.event.title }}</i>
        </template>
      </FullCalendar>
    </div>

  </div>
</template>

<style lang='css'>
h2 {
  margin: 0;
  font-size: 16px;
}

ul {
  margin: 0;
  padding: 0 0 0 1.5em;
}

li {
  margin: 1.5em 0;
  padding: 0;
}

b { /* used for event dates/times */
  margin-right: 3px;
}

.sidebar {
  width: 300px;
  line-height: 1.5;
  background: #eaf9ff;
  border-right: 1px solid #d3e2e8;
}

.sidebar-section {
  padding: 2em;
}

.calendar-wrap {
  flex-grow: 1;
  padding: 3em;
}

.fc { /* the calendar root */
  max-width: 1100px;
  margin: 0 auto;
}

</style>
