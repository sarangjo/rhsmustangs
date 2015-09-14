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

    public partial class EditDay : Form, AddPeriodListener
    {
        public Day schedDay;
        public EditDayListener l;
        public int index;
        
        /// <summary>
        /// Initializes the view.
        /// </summary>
        /// <param name="dayOfWeek">the current day of the week</param>
        public EditDay(EditDayListener l, bool updatedDay, int dayOfWeek)
        {
            InitializeComponent();
            periodsList.Items.Clear();
            
            this.l = l;

            // Checks if the day is updated
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

            removePeriodBtn.Enabled = false;

            // Sets the label
            dayOfWeekLbl.Text = Day.intToDay(dayOfWeek);

            this.index = -1;
        }

        public EditDay(EditDayListener l, Day day, int i) : this(l, day is UpdatedDay, 0)
        {
            this.schedDay = day;

            // Date
            if (day is UpdatedDay) {
                updatedDayPicker.Value = ((UpdatedDay)day).mDate;
            }

            // Periods
            periodsList.Items.Clear();
            foreach (Period p in schedDay.mPeriods)
            {
                periodsList.Items.Add(p);
            }

            // Groups
            refreshGroups();

            this.index = i;
        }

        private void addPeriodBtn_Click(object sender, EventArgs e)
        {
            new PeriodEditor(this, schedDay, updatedDayPicker.Visible).Show();
        }

        void AddPeriodListener.periodAdded(Period p, List<String> groups)
        {
            Console.WriteLine("Period added.");

            // Add period
            schedDay.mPeriods.Add(p);
            periodsList.Items.Add(p.ToString());
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
            l.dayEdited(schedDay, index);
        }

        private void removePeriodBtn_Click(object sender, EventArgs e)
        {
            schedDay.mPeriods.RemoveAt(periodsList.SelectedIndex);
            periodsList.Items.RemoveAt(periodsList.SelectedIndex);
            periodsList.ClearSelected();
        }

        private void periodsList_SelectedIndexChanged(object sender, EventArgs e)
        {
            removePeriodBtn.Enabled = periodsList.SelectedIndex >= 0;
        }
    }
}
