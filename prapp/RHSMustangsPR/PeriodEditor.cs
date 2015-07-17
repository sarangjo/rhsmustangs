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
        public AddSchedule addSched;

        public PeriodEditor(AddSchedule s)
        {
            InitializeComponent();
            addSched = s;

            setupGroups();

            //initializeGroupBoxes();
        }

        private void setupGroups()
        {
            groupsListBox.Items.Clear();
            for (int i = 0; i < addSched.sched.groups.Count; i++)
            {
                groupsListBox.Items.Add(addSched.sched.groups[i]);
            }
        }

        #region Radio Buttons
        private void otherRadio_CheckedChanged(object sender, EventArgs e)
        {
            periodN.Enabled = !otherRadio.Checked;
        }

        private void periodRadio_CheckedChanged(object sender, EventArgs e)
        {
            periodN.Enabled = periodRadio.Checked;
            shortName.Enabled = !periodRadio.Checked;
            periodName.Enabled = !periodRadio.Checked;
        }

        private void lunchRadio_CheckedChanged(object sender, EventArgs e)
        {
            periodN.Enabled = !lunchRadio.Checked;
            shortName.Enabled = !lunchRadio.Checked;
            periodName.Enabled = !lunchRadio.Checked;
        }
        #endregion

        private void addPeriodBtn_Click(object sender, EventArgs e)
        {
            String messageBoxText = "";
            int errors = 0;

            Period p = new Period();

            // Period
            if (periodRadio.Checked)
            {
                int n = -1;
                if (!int.TryParse(periodN.Text, out n) || n == 0)
                {
                    errors++;
                    messageBoxText += "Please enter a valid period number.\n";
                }
                else
                {
                    p.periodShort = periodN.Text;
                    p.overrideName = Period.OVERRIDE_DEFAULT;
                }
            }
            else if (lunchRadio.Checked)
            {
                p.periodShort = "LN";
                p.overrideName = Period.OVERRIDE_DEFAULT;
            }
            else if (otherRadio.Checked)
            {
                // Short
                if (shortName.Text.Length <= 2 || shortName.Text.Length > 0)
                    p.periodShort = shortName.Text;
                else
                {
                    errors++;
                    messageBoxText += "Short form text needs to be 1-2 characters.\n";
                }

                // Period Name
                if (periodName.Text.Length > 0)
                {
                    p.overrideName = periodName.Text;
                }
                else
                {
                    errors++;
                    messageBoxText += "Please enter a period name.\n";
                }
            }

            // Times
            // Parse integers
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

            if (errors > 0)
            {
                MessageBox.Show(((errors == 1) ? ("ERROR:\n") : ("ERRORS:\n")) + messageBoxText);
            }
            else
            {
                addSched.addPeriod(p);
                addSched.saveGroups(groupsListBox.Items);
                this.Close();
            }
        }

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

        private void shortName_TextChanged(object sender, EventArgs e)
        {
            if (shortName.Text.Trim().Length > 0)
            {
                otherRadio.Checked = true;
            }
        }

        private void removeGrpBtn_Click(object sender, EventArgs e)
        {
            if (groupsListBox.SelectedIndex > 0)
            {
                groupsListBox.Items.RemoveAt(groupsListBox.SelectedIndex);
            }
        }

        /*private void addPeriodButton_Click(object sender, EventArgs e)
        {
            String messageBoxText = "";
            int errors = 0;
            int periodN = 0;
            int startH = 0, startM = 0, endH = 0, endM = 0;




            // First, checks all 5 fields.
            // 1 - Short
            if (!lunchRadio.Checked && !hrRadio.Checked)
            {
                if (!int.TryParse(periodNumberText.Text, out periodN) || periodN == 0)
                {
                    if (shortFormText.Text.Trim().Length <= 0)
                    {
                        errors++;
                        messageBoxText += "Please enter a valid period number.\n";
                    }
                }
                else if (shortFormText.Text.Trim().Length <= 0 || shortFormText.Text.Trim().Length > 2)
                {
                    errors++;
                    messageBoxText += "Short form text needs to be 1-2 characters.\n";
                }
            }
            // Parse integers
            bool intErrors = !(int.TryParse(startHr.Text, out startH) &&
                int.TryParse(startMin.Text, out startM) &&
                int.TryParse(endHr.Text, out endH) &&
                int.TryParse(endMin.Text, out endM));
            if (intErrors)
            {
                messageBoxText += "Please enter valid numbers for the start and end times.\n";
                errors++;
            }
            else
            {
                // 3/4 - Start/End Times
                if (startH > 24 || startH < 0 || endH > 24 || endH < 0 || startM > 60 || startM < 0 || endM > 60 || endM < 0)
                {
                    messageBoxText += "Please enter valid numbers for the start and end times.\n";
                    errors++;
                }
            }
            if (!aRadio.Checked && !bRadio.Checked && !cRadio.Checked && !allRadio.Checked)
            {
                messageBoxText += "Please select a lunch type.\n";
                errors++;
            }

            if (errors > 0)
            {
                MessageBox.Show(((errors == 1) ? ("ERROR:\n") : ("ERRORS:\n")) + messageBoxText);
            }
            else
            {
                // Creating the String
                String p = "";

                p += (periodN == 0) ? "" : (periodN + " ");
                if (lunchRadio.Checked)
                {
                    p += "L";
                    if (aRadio.Checked) p += "A ";
                    else if (bRadio.Checked) p += "B ";
                    else if (cRadio.Checked) p += "C ";
                    else if (allRadio.Checked) p += "N ";
                }
                else
                {
                    p += (hrRadio.Checked) ? "HR " : "";
                    p += shortFormText.Text.Trim() + " ";
                    if (customNameText.Text.Trim().Length > 0)
                        p += customNameText.Text.Trim() + " ";
                    else
                        p += "- ";
                }
                p += startH + " " + startM + " ";
                p += endH + " " + endM + " ";
                if (aRadio.Checked) p += "a";
                else if (bRadio.Checked) p += "b";
                else if (cRadio.Checked) p += "c";
                else if (allRadio.Checked) p += "0";

                sched.addPeriod(p);
                this.Close();
            }

        }

        private void lunchRadio_CheckedChanged(object sender, EventArgs e)
        {
            if (lunchRadio.Checked)
            {
                periodNumberText.Text = "";
                shortFormText.Text = "";
                customNameText.Text = "";
            }
        }
        private void hrRadio_CheckedChanged(object sender, EventArgs e)
        {
            if (hrRadio.Checked)
            {
                periodNumberText.Text = "";
                shortFormText.Text = "";
                customNameText.Text = "";
            }
        }
        private void periodNumberText_Leave(object sender, EventArgs e)
        {
            if (periodNumberText.Text.Trim().Length > 0)
            {
                lunchRadio.Checked = false;
                hrRadio.Checked = false;
                shortFormText.Text = "";
                customNameText.Text = "";
            }
        }

        private void shortFormText_Leave(object sender, EventArgs e)
        {
            if (shortFormText.Text.Trim().Length > 0)
            {
                lunchRadio.Checked = false;
                hrRadio.Checked = false;
                periodNumberText.Text = "";
            }
        }

        private void customNameText_Leave(object sender, EventArgs e)
        {
            if (customNameText.Text.Trim().Length > 0)
            {
                lunchRadio.Checked = false;
                hrRadio.Checked = false;
                periodNumberText.Text = "";
            }

        }*/
    }
}
