namespace RHSMustangsPR
{
    partial class EditDay
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.label1 = new System.Windows.Forms.Label();
            this.addPeriodBtn = new System.Windows.Forms.Button();
            this.periodsList = new System.Windows.Forms.ListBox();
            this.label2 = new System.Windows.Forms.Label();
            this.groupsList = new System.Windows.Forms.ListBox();
            this.dayOfWeekLbl = new System.Windows.Forms.Label();
            this.saveDayBtn = new System.Windows.Forms.Button();
            this.updatedDayPicker = new System.Windows.Forms.DateTimePicker();
            this.removePeriodBtn = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(132, 9);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(48, 13);
            this.label1.TabIndex = 0;
            this.label1.Text = "Add Day";
            // 
            // addPeriodBtn
            // 
            this.addPeriodBtn.Location = new System.Drawing.Point(65, 85);
            this.addPeriodBtn.Name = "addPeriodBtn";
            this.addPeriodBtn.Size = new System.Drawing.Size(104, 23);
            this.addPeriodBtn.TabIndex = 1;
            this.addPeriodBtn.Text = "Add Period";
            this.addPeriodBtn.UseVisualStyleBackColor = true;
            this.addPeriodBtn.Click += new System.EventHandler(this.addPeriodBtn_Click);
            // 
            // periodsList
            // 
            this.periodsList.FormattingEnabled = true;
            this.periodsList.Location = new System.Drawing.Point(65, 114);
            this.periodsList.Name = "listBox1";
            this.periodsList.Size = new System.Drawing.Size(206, 95);
            this.periodsList.TabIndex = 2;
            this.periodsList.SelectedIndexChanged += new System.EventHandler(this.periodsList_SelectedIndexChanged);
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(149, 227);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(41, 13);
            this.label2.TabIndex = 3;
            this.label2.Text = "Groups";
            // 
            // groupsList
            // 
            this.groupsList.FormattingEnabled = true;
            this.groupsList.Location = new System.Drawing.Point(65, 250);
            this.groupsList.Name = "groupsList";
            this.groupsList.Size = new System.Drawing.Size(206, 95);
            this.groupsList.TabIndex = 4;
            // 
            // dayOfWeekLbl
            // 
            this.dayOfWeekLbl.AutoSize = true;
            this.dayOfWeekLbl.Location = new System.Drawing.Point(138, 67);
            this.dayOfWeekLbl.Name = "dayOfWeekLbl";
            this.dayOfWeekLbl.Size = new System.Drawing.Size(64, 13);
            this.dayOfWeekLbl.TabIndex = 5;
            this.dayOfWeekLbl.Text = "dayOfWeek";
            // 
            // saveDayBtn
            // 
            this.saveDayBtn.Location = new System.Drawing.Point(65, 353);
            this.saveDayBtn.Name = "saveDayBtn";
            this.saveDayBtn.Size = new System.Drawing.Size(206, 23);
            this.saveDayBtn.TabIndex = 6;
            this.saveDayBtn.Text = "Save Day";
            this.saveDayBtn.UseVisualStyleBackColor = true;
            this.saveDayBtn.Click += new System.EventHandler(this.saveDayBtn_Click);
            // 
            // updatedDayPicker
            // 
            this.updatedDayPicker.Location = new System.Drawing.Point(65, 35);
            this.updatedDayPicker.Name = "updatedDayPicker";
            this.updatedDayPicker.Size = new System.Drawing.Size(206, 20);
            this.updatedDayPicker.TabIndex = 8;
            // 
            // removePeriodBtn
            // 
            this.removePeriodBtn.Location = new System.Drawing.Point(172, 85);
            this.removePeriodBtn.Name = "removePeriodBtn";
            this.removePeriodBtn.Size = new System.Drawing.Size(99, 23);
            this.removePeriodBtn.TabIndex = 9;
            this.removePeriodBtn.Text = "Remove Period";
            this.removePeriodBtn.UseVisualStyleBackColor = true;
            this.removePeriodBtn.Click += new System.EventHandler(this.removePeriodBtn_Click);
            // 
            // AddDay
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(334, 390);
            this.Controls.Add(this.removePeriodBtn);
            this.Controls.Add(this.updatedDayPicker);
            this.Controls.Add(this.saveDayBtn);
            this.Controls.Add(this.dayOfWeekLbl);
            this.Controls.Add(this.groupsList);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.periodsList);
            this.Controls.Add(this.addPeriodBtn);
            this.Controls.Add(this.label1);
            this.Name = "AddDay";
            this.Text = "Schedule";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Button addPeriodBtn;
        private System.Windows.Forms.ListBox periodsList;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.ListBox groupsList;
        private System.Windows.Forms.Label dayOfWeekLbl;
        private System.Windows.Forms.Button saveDayBtn;
        private System.Windows.Forms.DateTimePicker updatedDayPicker;
        private System.Windows.Forms.Button removePeriodBtn;
    }
}