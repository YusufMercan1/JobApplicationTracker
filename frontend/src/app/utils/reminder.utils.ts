const REMINDER_DAYS = 3;

export function isDeadlineApproaching(deadline?: string): boolean {
  if (!deadline) {
    return false;
  }
  const d = parseDate(deadline);
  const today = startOfDay(new Date());
  const limit = addDays(today, REMINDER_DAYS);
  return d >= today && d <= limit;
}

export function isDeadlineOverdue(deadline?: string): boolean {
  if (!deadline) {
    return false;
  }
  return parseDate(deadline) < startOfDay(new Date());
}

function parseDate(value: string): Date {
  const d = new Date(value);
  return startOfDay(d);
}

function startOfDay(d: Date): Date {
  const copy = new Date(d);
  copy.setHours(0, 0, 0, 0);
  return copy;
}

function addDays(d: Date, days: number): Date {
  const copy = new Date(d);
  copy.setDate(copy.getDate() + days);
  return copy;
}
