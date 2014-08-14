using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace RHSMustangsPR
{
    public class Schedule
    {
        private string date;
        private List<Period> periods = new List<Period>();
        private List<string> groups = new List<string>();

        public void addPeriod(Period p)
        {
            periods.Add(p);
        }
    }
}
