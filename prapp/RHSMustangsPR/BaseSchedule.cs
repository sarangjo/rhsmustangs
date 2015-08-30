using Parse;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace RHSMustangsPR
{
    public class BaseSchedule
    {
        public Day[] mDays;

        public BaseSchedule()
        {
            mDays = new Day[5];
        }

        public async void save()
        {
            for (int i = 0; i < mDays.Length; i++)
            {
                Day day = mDays[i];
                if (day != null && day.saved)
                {
                    var query = ParseObject.GetQuery("BaseDay").WhereEqualTo("dayOfWeek", day.mDayOfWeek);
                    IEnumerable<ParseObject> results = await query.FindAsync();

                    if (results.Count() > 0)
                    {
                        ParseObject parseDay = results.ElementAt(0);

                        // Delete old periods
                        var periodsRelation = parseDay.GetRelation<ParseObject>("periods");
                        IEnumerable<ParseObject> parsePeriods = await periodsRelation.Query.FindAsync();

                        foreach (ParseObject parsePeriod in parsePeriods)
                        {
                            await parsePeriod.DeleteAsync();
                        }

                        // Resave and add new periods
                        List<ParseObject> newParsePeriods = new List<ParseObject>();

                        for (int j = 0; j < day.mPeriods.Count; j++)
                        {
                            newParsePeriods.Add(day.mPeriods[j].toParseObject());
                        }

                        foreach (ParseObject obj in newParsePeriods)
                        {
                            await obj.SaveAsync();
                            periodsRelation.Add(obj);
                        }

                        // Save day
                        await parseDay.SaveAsync();
                    }
                }
                Console.WriteLine(Day.intToDay(i + 2) + " saved.");
            }
            // All days saved
            Console.WriteLine("Schedule saved.");
        }
    }
}