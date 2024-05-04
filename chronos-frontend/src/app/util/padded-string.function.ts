export function paddedString(num: number | undefined, length: number, handleNegative = false): string {
  if (num === undefined) {
    num = 0
  }
  if (handleNegative && num < 0) {
    num = 0;
  }
  num = Math.floor(num);
  return String(num).padStart(length, '0');
}
