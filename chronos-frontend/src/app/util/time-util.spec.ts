import {TimeUtil} from "./time-util";

describe('TimeUtil', () => {

  fdescribe('find the earliest Date', () => {

    it('from several distinct values', () => {
      const dates: Array<Date> = [
        new Date("1213-01-17"),
        new Date("1786-10-30"),
        new Date("-000077-08-14"),
        new Date("0877-03-02"),
      ];
      expect(TimeUtil.getEarliestDate(dates)).toEqual(new Date("-000077-08-14"));
    })

    it('from an empty array', () => {
      const dates: Array<Date> = [];
      expect(() => TimeUtil.getEarliestDate(dates)).toThrow();
    })

  });

  describe('find the latest Date', () => {

    it('from several distinct values', () => {
      const dates: Array<Date> = [
        new Date("1213-01-17"),
        new Date("1786-10-30"),
        new Date("-000077-08-14"),
        new Date("0877-03-02"),
      ];
      expect(TimeUtil.getLatestDate(dates)).toEqual(new Date("1786-10-30"));
    })

  });

  fdescribe('gets year for a date', () => {

    it('for dates AD', () => {
      expect(TimeUtil.getDateForYear(25).toISOString()).toMatch(/0025-01-01T.*/);
      expect(TimeUtil.getDateForYear(725).toISOString()).toMatch(/0725-01-01T.*/);
      expect(TimeUtil.getDateForYear(1725).toISOString()).toMatch(/1725-01-01T.*/);
    })

    it('for dates BC', () => {
      expect(TimeUtil.getDateForYear(-25).toISOString()).toMatch(/-000025-01-01T.*/);
      expect(TimeUtil.getDateForYear(-525).toISOString()).toMatch(/-000525-01-01T.*/);
      expect(TimeUtil.getDateForYear(-2125).toISOString()).toMatch(/-002125-01-01T.*/);
    })

  });

});
