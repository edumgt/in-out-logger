<script setup lang="ts">
import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import { computed, h, nextTick, onMounted, ref } from 'vue'
import axios from '@/utils/axios.ts'
import { useProgress } from '@/utils/proxy.ts'
import { useStore } from 'vuex'
import { InputValue, selectOptions, SelectOption } from '@/stores/vuex/modules/modal.ts'
import { DateSelectArg, EventApi, EventClickArg } from '@fullcalendar/core'
import { dateToNumber } from '@/utils/string.ts'

type Direction = 'asc' | 'desc'

const calendarRef = ref<InstanceType<typeof FullCalendar>>()
const calendarApi = computed(() => (calendarRef.value as InstanceType<typeof FullCalendar>).getApi())
const currentEvents = ref<EventApi[]>([])


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
        h('p', '배경색 지정'),
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
        title: inputValue,
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
        backgroundColor: selectBoxValue.value
      })
    }
  })

}

const handleEventClick = (clickInfo: EventClickArg) => {
  store.commit('setModal', {
    isOpen: true,
    modalType: 'alert',
    content: h('p', '일정을 삭제할까요?'),
    onConfirm: async () => {
      const response = await axios.delete(`/api/calendar/events/${clickInfo.event.id}`)
      console.log(response)
      clickInfo.event.remove()
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
const calendarOptions = ref<object>({
  plugins: [
    dayGridPlugin,
    timeGridPlugin,
    interactionPlugin // dateClick
  ],
  headerToolbar: {
    left: 'prev',
    center: 'title',
    right: 'next'
  },
  initialView: 'dayGridMonth',
  initialDate: new Date(),
  editable: true,
  selectable: true,
  selectMirror: true,
  dayMaxEvents: true,
  weekends: true,
  select: handleDateSelect,
  eventClick: handleEventClick,
  eventsSet: handleEvents
  /* you can update a remote database when these fire:
  eventAdd:
  eventChange:
  eventRemove:
  */
})
const fetchInitialEvents = async () => {
  const response = await axios.get('/api/calendar/events')
  return response.data
}

onMounted(async () => {
  await nextTick()
  const events = await fetchInitialEvents()
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
})

const handleCheckIn = async () => {
  try {
    await axios.post('/api/commutes')
  } catch (e: any) {
    store.commit('setModal', {
      isOpen: true,
      content: h('p', e.response.data),
      modalType: 'alert'
    })
    throw e
  }
}
const handleCheckOut = async () => {
  try {
    await axios.patch('/api/commutes')
  } catch (e: any) {
    store.commit('setModal', {
      isOpen: true,
      content: h('p', e.response.data)
    })
    throw e
  }
}
const checkInProgress = useProgress(handleCheckIn)
const checkOutProgress = useProgress(handleCheckOut)

const store = useStore()

const handleNavigateCalendar = (eventStartDate: string) => {
  calendarApi.value.gotoDate(eventStartDate)
}

</script>

<template>
  <div class='main-wrap'>
    <div class='sidebar'>
      <div class='sidebar-section flex flex-col gap-2'>
        <button class="btn green small" @click="checkInProgress">출근</button>
        <button class="btn purple small" @click="checkOutProgress">퇴근</button>
        <button class="btn cyan block mini" @click="">출근부 보기</button>
      </div>
      <div class='sidebar-section'>
        <label> <input type='checkbox' :checked='calendarOptions.weekends' @change='handleWeekendsToggle' /> toggle
          weekends </label>
      </div>
      <div class='sidebar-section'>
        <div class="flex gap-2">
          <h2>All Events ({{ currentEvents.length }})</h2>
          <div class="mt-1 flex gap-1">
            <input @change="handleClickCheckbox" :checked="direction === 'asc'" id="direction" type="checkbox" />
            <label for="direction">오름차순</label>
          </div>
        </div>
        <ul>
          <li v-for='event in currentEvents' :key='event.id'>
            <div class="cursor-pointer" @click="() => handleNavigateCalendar(event.startStr)">
              <b>{{ event.startStr }}</b> <i>{{ event.title }}</i>
            </div>
          </li>
        </ul>
      </div>
    </div>
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
