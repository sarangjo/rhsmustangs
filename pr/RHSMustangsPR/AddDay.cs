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
    public interface AddPeriodListener
    {
        void periodAdded(Period p, List<String> groups);
    }

    public partial class AddDay : Form, AddPeriodListener
    {
        public Day schedDay;
        public AddDayListener l;
        
        /// <summary>
        /// Initializes the view.
        /// </summary>
        /// <param name="dayOfWeek">the current day of the week</param>
        public AddDay(AddDayListener l, bool updatedDay, int dayOfWeek)
        {
            InitializeComponent();
            listBox1.Items.Clear();
            
            this.l = l;

            if (updatedDay)
            {
                updatedDayPicker.Visible = true;
                this.schedDay = new UpdatedDay(updatedDayPicker.Value);
            }
            else
            {
                updatedDayPicker.Visible = false;
                this.schedDay = new Day(dayOfWeek);
            }

            // Sets the label
            dayOfWeekLbl.Text = Day.intToDay(dayOfWeek);
        }

        private void addPeriodBtn_Click(object sender, EventArgs e)
        {
            new PeriodEditor(this, schedDay, updatedDayPicker.Visible).Show();
        }

        void AddPeriodListener.periodAdded(Period p, List<String> groups)
        {
            Console.WriteLine("Period added.");

            // Add period
            schedDay.AddPeriod(p);
            listBox1.Items.Add(p.ToString());
            // Groups
            schedDay.setGroups(groups);
            refreshGroups();
        }

        /// <summary>
        /// Updates the list with the groups
        /// </summary>
        private void refreshGroups()
        {
            groupsList.Items.Clear();
            foreach (string s in schedDay.mGroups)
            {
                groupsList.Items.Add(s);
            }
        }

        /// <summary>
        /// Saves this base day.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void saveDayBtn_Click(object sender, EventArgs e)
        {
            this.Close();
            schedDay.save();
            if (updatedDayPicker.Visible)
                ((UpdatedDay)schedDay).mDate = updatedDayPicker.Value;
            l.dayAdded(schedDay);
        }
    }
}
