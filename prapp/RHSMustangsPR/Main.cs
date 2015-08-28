using Parse;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace RHSMustangsPR
{
    public partial class Main : Form
    {
        public Main()
        {
            InitializeComponent();

            ParseClient.Initialize("VTncYgZyAW6wV67VNiXAIAYzotG4EZaz0kfzPCbt", "MlsKCUqqoD7K0imrzz7vFydnuxBAJGGigdZYzDEp");

            //addDay();
            //addPeriods();
            //setup();
        }

        private async void baseSetup()
        {
            ParseQuery<ParseObject> q = new ParseQuery<ParseObject>("BaseDay");
            ParseObject mon = await q.GetAsync("PLdsIl6HqY");
            ParseObject tues = await q.GetAsync("0sGWu5Ndbz");
            ParseObject thur = await q.GetAsync("ltsjoJFM6s");
            ParseObject fri = await q.GetAsync("LAeVb1EQpN");

            
            IList<ParseObject> norm = new List<ParseObject>();
            norm.Add(mon);
            norm.Add(tues);
            norm.Add(thur);
            norm.Add(fri);

            // mon,tue,thur,fri
            ParseQuery<ParseObject> periodQ = new ParseQuery<ParseObject>("Period");
            IList<ParseObject> periods = new List<ParseObject>();
            periods.Add(await periodQ.GetAsync("iK9iwAfmzZ"));
            periods.Add(await periodQ.GetAsync("OEnAB6m7Zd"));
            periods.Add(await periodQ.GetAsync("at7yUbIGpp"));
            periods.Add(await periodQ.GetAsync("4HUwVLFLRH"));
            periods.Add(await periodQ.GetAsync("Y4LgUANB4d"));
            periods.Add(await periodQ.GetAsync("hq0KiURZ4I"));
            periods.Add(await periodQ.GetAsync("bw3cdD7fU2"));
            periods.Add(await periodQ.GetAsync("qCtfQQcuBg"));
            periods.Add(await periodQ.GetAsync("1pV4CVdQuD"));

            foreach (ParseObject day in norm)
            {
                foreach (ParseObject per in periods)
                {
                    day.GetRelation<ParseObject>("periods").Add(per);
                }
                await day.SaveAsync();
            }

            // wed

        }

        private async void addDay()
        {
            ParseObject day = new ParseObject("UpdatedDay");

            day["date"] = new DateTime(2015, 8, 12);

            await day.SaveAsync();

            Console.WriteLine();
        }

        private async void addPeriods()
        {
            ParseQuery<ParseObject> query = ParseObject.GetQuery("UpdatedDay");
            ParseObject day = await query.GetAsync("fOiu9Dcwnj");

            ParseQuery<ParseObject> periodsQuery = ParseObject.GetQuery("Period");
            IList<ParseObject> periods = new List<ParseObject>();
            periods.Add(await periodsQuery.GetAsync("hxli6MBviB"));

            ParseRelation<ParseObject> relation = day.GetRelation<ParseObject>("periods");
            foreach (ParseObject p in periods)
            {
                relation.Add(p);
            }
            await day.SaveAsync();

            Console.WriteLine();
        }

        public async void setup()
        {
            ParseQuery<ParseObject> query = ParseObject.GetQuery("UpdatedDay");
            ParseObject updatedDay = await query.GetAsync("oyFbym8DDu");
            
            /*
            // Setup groups
            IList<string> groupNames = new List<string>();
            groupNames.Add("Grp1");
            groupNames.Add("Grp2");
            updatedDay["groupNames"] = groupNames;

            ParseQuery<ParseObject> query2 = ParseObject.GetQuery("Period");
            List<ParseObject> periods = new List<ParseObject>();
            periods.Add(await query2.GetAsync("P35hnqAkcp"));
            periods.Add(await query2.GetAsync("j82dBjyU8h"));
            periods.Add(await query2.GetAsync("K8KumlCQ2Z"));

            var relation = updatedDay.GetRelation<ParseObject>("periods");
            foreach (ParseObject p in periods)
            {
                relation.Add(p);
            }*/
            await updatedDay.SaveAsync();

            Console.WriteLine();
        }

        private void addSchedule_Click(object sender, EventArgs e)
        {
            AddSchedule s = new AddSchedule();
            s.Show();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            wedSetup();
        }

        private async void wedSetup()
        {
            ParseQuery<ParseObject> q = new ParseQuery<ParseObject>("BaseDay");
            ParseObject wed = await q.GetAsync("RXNWtJlBr8");

            ParseQuery<ParseObject> periodQ = new ParseQuery<ParseObject>("Period");
            IList<ParseObject> periods = new List<ParseObject>();
            periods.Add(await periodQ.GetAsync("fyB5TjBd0g"));
            periods.Add(await periodQ.GetAsync("ZEOA4FVbq8"));
            periods.Add(await periodQ.GetAsync("tMTZ8OnTVs"));
            periods.Add(await periodQ.GetAsync("e7hJvB0LtL"));
            periods.Add(await periodQ.GetAsync("KqWrXpnRYA"));
            periods.Add(await periodQ.GetAsync("1FcRexSFdL"));
            periods.Add(await periodQ.GetAsync("F6xlAur10X"));
            periods.Add(await periodQ.GetAsync("j4CdacCATQ"));

            foreach (ParseObject per in periods)
            {
                wed.GetRelation<ParseObject>("periods").Add(per);
            }
            await wed.SaveAsync();

        }
    }
}
