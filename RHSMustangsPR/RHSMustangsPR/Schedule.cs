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
        public List<Period> periods = new List<Period>();
        public List<string> groups = new List<string>();

        public Schedule()
        {
            groups.Add("Everyone");
        }

        public void addPeriod(Period p)
        {
            periods.Add(p);
        }
    }
}
