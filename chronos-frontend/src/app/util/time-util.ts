export class TimeUtil {

  public static getEarliestDate(dates: Array<Date>): Date {
    if (!dates || dates.length === 0) {
      throw new Error("No dates to find a minimum for");
    }
    return dates.reduce(function (a, b) { return a < b ? a : b; });
  }

  public static getLatestDate(dates: Array<Date>): Date {
    if (!dates || dates.length === 0) {
      throw new Error("No dates to find a maximum for");
    }
    return dates.reduce(function (a, b) { return a > b ? a : b; });
  }

  public static getDateForYear(year: number): Date {
    const date = new Date();
    date.setFullYear(year, 0, 1);
    return date
  }

}
