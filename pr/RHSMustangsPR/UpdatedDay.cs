using Parse;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace RHSMustangsPR
{
    public class UpdatedDay : Day
    {
        public DateTime mDate;

        public UpdatedDay(DateTime date) : base(Day.getInt(date.DayOfWeek))
        {
            mDate = date;
        }

        public ParseObject toParseObject()
        {
            ParseObject obj = new ParseObject("UpdatedDay");

            obj["date"] = Updates.DateToString(mDate);
            
            string[] gNames = Day.parseGroupNames(mGroups);
            if (gNames != null) obj["groupNames"] = gNames;

            return obj;
        }

        public string ToString()
        {
            return Updates.DateToString(mDate);
        }

        public UpdatedDay Copy()
        {
            UpdatedDay day = new UpdatedDay(mDate);
            // TODO: implement
            return day;
        }
    }
}
