using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Parse;

namespace RHSMustangsPR
{
    public class Updates
    {
        public List<UpdatedDay> mUpdatedDays;

        public Updates()
        {
            mUpdatedDays = new List<UpdatedDay>();
        }

        /// <summary>
        /// Saves this day to Parse.
        /// </summary>
        public async void save()
        {
            foreach (UpdatedDay uDay in mUpdatedDays)
            {
                // Delete old day in that date
                var query = from updatedDay in ParseObject.GetQuery("UpdatedDay")
                           where updatedDay.Get<string>("date") == Updates.DateToString(uDay.mDate)
                           select updatedDay;
                
                //ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("UpdatedDay").WhereEqualTo("date", uDay.mDate);
                IEnumerable<ParseObject> results = await query.FindAsync();

                ParseRelation<ParseObject> periodsRelation;
                if (results.Count() > 0)
                {
                    ParseObject obj = results.ElementAt(0);

                    // Delete old day's periods
                    periodsRelation = obj.GetRelation<ParseObject>("periods");
                    IEnumerable<ParseObject> parsePeriods = await periodsRelation.Query.FindAsync();
                    foreach (ParseObject period in parsePeriods)
                    {
                        await period.DeleteAsync();
                    }
                    await obj.DeleteAsync();
                }
                
                // Save new day
                ParseObject parseDay = uDay.toParseObject();
                await parseDay.SaveAsync();

                List<ParseObject> newParsePeriods = new List<ParseObject>();
                for (int j = 0; j < uDay.mPeriods.Count; j++)
                {
                    newParsePeriods.Add(uDay.mPeriods[j].toParseObject());
                }

                periodsRelation = parseDay.GetRelation<ParseObject>("periods");
                foreach (ParseObject per in newParsePeriods)
                {
                    await per.SaveAsync();
                    periodsRelation.Add(per);
                }

                // Save day
                await parseDay.SaveAsync();

                Console.WriteLine("Saved " + Updates.DateToString(uDay.mDate));
                UpdatesEditor.form.Close();
            }
        }

        /// <summary>
        /// Converts to the standard format.
        /// </summary>
        /// <param name="dateTime"></param>
        /// <returns></returns>
        public static string DateToString(DateTime dateTime)
        {
            return dateTime.Year + "-" + dateTime.Month.ToString("D2") + "-" + dateTime.Day.ToString("D2");
        }
    }
}
