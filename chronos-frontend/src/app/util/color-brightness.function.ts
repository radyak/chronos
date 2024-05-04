const cache: Map<string, number> = new Map<string, number>();

export function colorBrightness(hexColor: string): number {
  if (cache.has(hexColor)) {
    return cache.get(hexColor)!;
  }
  const len = hexColor.length === 7 ? 2 : 1;
  const r = parseInt(hexColor.slice(1 + 0 * len, 1 + 1 * len), 16);
  const g = parseInt(hexColor.slice(1 + 1 * len, 1 + 2 * len), 16);
  const b = parseInt(hexColor.slice(1 + 2 * len, 1 + 3 * len), 16);
  const brightness = (r + g + b) / (Math.pow(16, len) * 3);
  cache.set(hexColor, brightness);
  return brightness;
}

export function isBright(hexColor: string) {
  return colorBrightness(hexColor) > 0.60;
}
