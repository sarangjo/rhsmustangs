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
            addPeriods();
            //setup();
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
    }
}
