export const vacation = {
  ALL_DAY: '종일 휴가',
  MORNING_HALF_DAY: '오전 반차',
  AFTERNOON_HALF_DAY: '오후 반차',
  SICK_LEAVE: '병가',
  UNPAID_LEAVE: '무급 휴가',
  SPECIAL_LEAVE: '특별 휴가'
} as const

export type VacationType = vacation[keyof typeof vacation]
