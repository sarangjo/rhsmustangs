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
    public partial class PeriodEditor : Form
    {
        public AddPeriodListener l;
        public Day baseSched;
        public Period p;

        /// <summary>
        /// Creates a new Period.
        /// </summary>
        /// <param name="s"></param>
        public PeriodEditor(AddPeriodListener l, Day s)
        {
            InitializeComponent();
            this.l = l;
            this.baseSched = s;

            p = new Period();

            setupGroups();

            //initializeGroupBoxes();
        }

        /// <summary>
        /// Sets up the groups from the previously created groups.
        /// </summary>
        private void setupGroups()
        {
            groupsListBox.Items.Clear();
            for (int i = 0; i < baseSched.mGroups.Count; i++)
            {
                groupsListBox.Items.Add(baseSched.mGroups[i]);
            }
        }

        /// <summary>
        /// Adds a new group.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void newGroupBtn_Click(object sender, EventArgs e)
        {
            if (!newGroupText.Text.Trim().Equals(""))
            {
                try
                {
                    groupsListBox.Items.Add(newGroupText.Text);
                    newGroupText.Text = "";
                    groupsListBox.SelectedIndex = groupsListBox.Items.Count - 1;
                }
                catch (Exception ex)
                {
                }
            }
        }

        /// <summary>
        /// Removes a group if selected
        /// </summary>
        private void removeGrpBtn_Click(object sender, EventArgs e)
        {
            if (groupsListBox.SelectedIndex > 0)
            {
                groupsListBox.Items.RemoveAt(groupsListBox.SelectedIndex);
            }
        }

        /// <summary>
        /// Saves the period.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void savePeriodBtn_Click(object sender, EventArgs e)
        {
            String messageBoxText = "";
            int errors = 0;

            // Short
            if (shortName.Text.Length == 2)
                p.periodShort = shortName.Text.ToUpper();
            else
            {
                errors++;
                messageBoxText += "Short form text needs to be 2 letters.\n";
            }
            // Period Name
            if (periodName.Text.Length > 0)
            {
                p.periodName = periodName.Text;
            }
            else
            {
                errors++;
                messageBoxText += "Please enter a period name.\n";
            }

            // Times
            bool intErrors = !(int.TryParse(startHText.Text, out p.startH) &&
                int.TryParse(startMText.Text, out p.startM) &&
                int.TryParse(endHText.Text, out p.endH) &&
                int.TryParse(endMText.Text, out p.endM));
            if (intErrors)
            {
                messageBoxText += "Please enter valid numbers for the start and end times.\n";
                errors++;
            }
            else
            {
                // 3/4 - Start/End Times
                if (p.startH > 24 || p.startH < 0 || p.endH > 24 || p.endH < 0 || p.startM > 60 || p.startM < 0 || p.endM > 60 || p.endM < 0)
                {
                    messageBoxText += "Please enter valid numbers for the start and end times.\n";
                    errors++;
                }
            }

            // Group
            p.groupN = (groupsListBox.SelectedIndex < 0) ? 0 : groupsListBox.SelectedIndex;

            // Finish up
            if (errors > 0)
            {
                MessageBox.Show(((errors == 1) ? ("ERROR:\n") : ("ERRORS:\n")) + messageBoxText);
            }
            else
            {
                // Great success!
                this.Close();
                this.l.periodAdded(p, BaseEditor.ItemsToList(groupsListBox.Items));
            }
        }
    }
}
