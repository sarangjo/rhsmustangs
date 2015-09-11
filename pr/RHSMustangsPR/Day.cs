using Parse;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace RHSMustangsPR
{
    public class Day
    {
        /// <summary>
        /// Converts an integer day of week to the actual day of the week.
        /// </summary>
        /// <param name="n"></param>
        /// <returns></returns>
        public static string intToDay(int n)
        {
            switch (n)
            {
                case 1:
                    return "Sunday";
                case 2:
                    return "Monday";
                case 3:
                    return "Tuesday";
                case 4:
                    return "Wednesday";
                case 5:
                    return "Thursday";
                case 6:
                    return "Friday";
                case 7:
                    return "Saturday";
            }
            return "No day";
        }

        public int mDayOfWeek;

        public IList<Period> mPeriods;
        public IList<string> mGroups;

        public bool saved = false;
        
        public Day(int day) : this(day, null)
        {
        }

        public Day(int day, IList<string> groups)
        {
            mPeriods = new List<Period>();
            mDayOfWeek = day;
            if (groups == null || groups.Count == 0)
            {
                mGroups = new List<string>();
                mGroups.Add("Everyone");
            }
            else
            {
                mGroups = groups;
            }
        }

        /// <summary>
        /// Adds a period to the day.
        /// </summary>
        /// <param name="p"></param>
        public void AddPeriod(Period p)
        {
            mPeriods.Add(p);
        }

        /// <summary>
        /// Sets the groups.
        /// </summary>
        /// <param name="groupItems"></param>
        public void setGroups(List<string> groupItems)
        {
            mGroups.Clear();
            for (int i = 0; i < groupItems.Count; i++)
            {
                mGroups.Add(groupItems[i].ToString());
            }
        }

        public void save()
        {
            saved = true;
        }

        public static int getInt(DayOfWeek dayOfWeek)
        {
            return (int)dayOfWeek + 1;
        }

        public static Day newFromParse(ParseObject parseDay,
            IEnumerable<ParseObject> parsePeriods)
        {
            int dayOfWeek = ((int)parseDay["dayOfWeek"]);
            IList<string> groups = (IList<string>)parseDay["groupNames"];
            Day d = new Day(dayOfWeek, groups);

            foreach (ParseObject period in parsePeriods)
            {
                d.AddPeriod(Period.newFromParse(period));
            }

            return d;
        }

        /// <summary>
        /// Converts to Parse-ready group names.
        /// </summary>
        /// <param name="mGroups"></param>
        /// <returns></returns>
        public static string[] parseGroupNames(IList<string> mGroups)
        {
            if (mGroups.Count == 1)
            {
                return null;
            }
            string[] arr = new string[mGroups.Count - 1];
            for (int i = 1; i < mGroups.Count; i++ )
            {
                arr[i - 1] = mGroups[i];
            }
            return arr;
        }
    }
}
