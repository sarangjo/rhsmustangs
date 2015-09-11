using Parse;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace RHSMustangsPR
{
    class Holidays
    {
        public IList<Holiday> mHolidays;

        public Holidays()
        {
            mHolidays = new List<Holiday>();
        }

        public void loadFromParse()
        {

        }

        public async void saveToParse()
        {
            foreach (Holiday hol in mHolidays)
            {
                ParseObject obj = hol.getParseObject();
                await obj.SaveAsync();
            }
        }
    }
}
