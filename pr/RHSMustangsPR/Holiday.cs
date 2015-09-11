using Parse;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace RHSMustangsPR
{
    public class Holiday
    {
        public DateTime mStart;
        public DateTime mEnd;
        public String mName;

        public Holiday(DateTime start, DateTime end, String name)
        {
            mStart = start;
            mEnd = end;
            mName = name;
        }

        public ParseObject getParseObject()
        {
            ParseObject obj = new ParseObject("Holiday");
            obj["start"] = Updates.DateToString(mStart);
            obj["end"] = Updates.DateToString(mEnd);
            obj["name"] = mName;
            return obj;
        }
        
        public string ToString()
        {
            return mName + ", " + (mStart) + " to " + (mEnd);
        }
    }
}
