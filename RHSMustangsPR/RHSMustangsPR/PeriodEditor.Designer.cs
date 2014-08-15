namespace RHSMustangsPR
{
    partial class PeriodEditor
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
            this.periodRadio = new System.Windows.Forms.RadioButton();
            this.lunchRadio = new System.Windows.Forms.RadioButton();
            this.periodN = new System.Windows.Forms.TextBox();
            this.shortName = new System.Windows.Forms.TextBox();
            this.periodName = new System.Windows.Forms.TextBox();
            this.shortLabel = new System.Windows.Forms.Label();
            this.nameLabel = new System.Windows.Forms.Label();
            this.startHText = new System.Windows.Forms.TextBox();
            this.startMText = new System.Windows.Forms.TextBox();
            this.endMText = new System.Windows.Forms.TextBox();
            this.endHText = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.addPeriodBtn = new System.Windows.Forms.Button();
            this.groupsListBox = new System.Windows.Forms.ListBox();
            this.newGroupBtn = new System.Windows.Forms.Button();
            this.newGroupText = new System.Windows.Forms.TextBox();
            this.label3 = new System.Windows.Forms.Label();
            this.otherRadio = new System.Windows.Forms.RadioButton();
            this.SuspendLayout();
            // 
            // periodRadio
            // 
            this.periodRadio.AutoSize = true;
            this.periodRadio.Location = new System.Drawing.Point(69, 28);
            this.periodRadio.Name = "periodRadio";
            this.periodRadio.Size = new System.Drawing.Size(55, 17);
            this.periodRadio.TabIndex = 0;
            this.periodRadio.TabStop = true;
            this.periodRadio.Text = "Period";
            this.periodRadio.UseVisualStyleBackColor = true;
            this.periodRadio.CheckedChanged += new System.EventHandler(this.periodRadio_CheckedChanged);
            // 
            // lunchRadio
            // 
            this.lunchRadio.AutoSize = true;
            this.lunchRadio.Location = new System.Drawing.Point(69, 48);
            this.lunchRadio.Name = "lunchRadio";
            this.lunchRadio.Size = new System.Drawing.Size(55, 17);
            this.lunchRadio.TabIndex = 1;
            this.lunchRadio.TabStop = true;
            this.lunchRadio.Text = "Lunch";
            this.lunchRadio.UseVisualStyleBackColor = true;
            this.lunchRadio.CheckedChanged += new System.EventHandler(this.lunchRadio_CheckedChanged);
            // 
            // periodN
            // 
            this.periodN.Enabled = false;
            this.periodN.Location = new System.Drawing.Point(126, 27);
            this.periodN.Name = "periodN";
            this.periodN.Size = new System.Drawing.Size(29, 20);
            this.periodN.TabIndex = 2;
            // 
            // shortName
            // 
            this.shortName.Location = new System.Drawing.Point(265, 49);
            this.shortName.Name = "shortName";
            this.shortName.Size = new System.Drawing.Size(36, 20);
            this.shortName.TabIndex = 3;
            this.shortName.TextChanged += new System.EventHandler(this.shortName_TextChanged);
            // 
            // periodName
            // 
            this.periodName.Location = new System.Drawing.Point(265, 71);
            this.periodName.Name = "periodName";
            this.periodName.Size = new System.Drawing.Size(87, 20);
            this.periodName.TabIndex = 4;
            // 
            // shortLabel
            // 
            this.shortLabel.AutoSize = true;
            this.shortLabel.Location = new System.Drawing.Point(182, 53);
            this.shortLabel.Name = "shortLabel";
            this.shortLabel.Size = new System.Drawing.Size(63, 13);
            this.shortLabel.TabIndex = 5;
            this.shortLabel.Text = "Short Name";
            // 
            // nameLabel
            // 
            this.nameLabel.AutoSize = true;
            this.nameLabel.Location = new System.Drawing.Point(182, 74);
            this.nameLabel.Name = "nameLabel";
            this.nameLabel.Size = new System.Drawing.Size(68, 13);
            this.nameLabel.TabIndex = 6;
            this.nameLabel.Text = "Period Name";
            // 
            // startHText
            // 
            this.startHText.Location = new System.Drawing.Point(194, 111);
            this.startHText.Name = "startHText";
            this.startHText.Size = new System.Drawing.Size(29, 20);
            this.startHText.TabIndex = 7;
            // 
            // startMText
            // 
            this.startMText.Location = new System.Drawing.Point(229, 111);
            this.startMText.Name = "startMText";
            this.startMText.Size = new System.Drawing.Size(29, 20);
            this.startMText.TabIndex = 8;
            // 
            // endMText
            // 
            this.endMText.Location = new System.Drawing.Point(229, 137);
            this.endMText.Name = "endMText";
            this.endMText.Size = new System.Drawing.Size(29, 20);
            this.endMText.TabIndex = 12;
            // 
            // endHText
            // 
            this.endHText.Location = new System.Drawing.Point(194, 137);
            this.endHText.Name = "endHText";
            this.endHText.Size = new System.Drawing.Size(29, 20);
            this.endHText.TabIndex = 11;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(133, 114);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(55, 13);
            this.label1.TabIndex = 13;
            this.label1.Text = "Start Time";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(133, 140);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(52, 13);
            this.label2.TabIndex = 14;
            this.label2.Text = "End Time";
            // 
            // addPeriodBtn
            // 
            this.addPeriodBtn.Location = new System.Drawing.Point(170, 297);
            this.addPeriodBtn.Name = "addPeriodBtn";
            this.addPeriodBtn.Size = new System.Drawing.Size(75, 23);
            this.addPeriodBtn.TabIndex = 15;
            this.addPeriodBtn.Text = "Add Period";
            this.addPeriodBtn.UseVisualStyleBackColor = true;
            this.addPeriodBtn.Click += new System.EventHandler(this.addPeriodBtn_Click);
            // 
            // groupsListBox
            // 
            this.groupsListBox.FormattingEnabled = true;
            this.groupsListBox.Items.AddRange(new object[] {
            "Everyone"});
            this.groupsListBox.Location = new System.Drawing.Point(55, 187);
            this.groupsListBox.Name = "groupsListBox";
            this.groupsListBox.Size = new System.Drawing.Size(120, 95);
            this.groupsListBox.TabIndex = 16;
            // 
            // newGroupBtn
            // 
            this.newGroupBtn.Location = new System.Drawing.Point(296, 204);
            this.newGroupBtn.Name = "newGroupBtn";
            this.newGroupBtn.Size = new System.Drawing.Size(56, 25);
            this.newGroupBtn.TabIndex = 17;
            this.newGroupBtn.Text = "Add";
            this.newGroupBtn.UseVisualStyleBackColor = true;
            this.newGroupBtn.Click += new System.EventHandler(this.newGroupBtn_Click);
            // 
            // newGroupText
            // 
            this.newGroupText.Location = new System.Drawing.Point(186, 207);
            this.newGroupText.Name = "newGroupText";
            this.newGroupText.Size = new System.Drawing.Size(100, 20);
            this.newGroupText.TabIndex = 18;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(95, 171);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(41, 13);
            this.label3.TabIndex = 19;
            this.label3.Text = "Groups";
            // 
            // otherRadio
            // 
            this.otherRadio.AutoSize = true;
            this.otherRadio.Location = new System.Drawing.Point(240, 27);
            this.otherRadio.Name = "otherRadio";
            this.otherRadio.Size = new System.Drawing.Size(51, 17);
            this.otherRadio.TabIndex = 20;
            this.otherRadio.TabStop = true;
            this.otherRadio.Text = "Other";
            this.otherRadio.UseVisualStyleBackColor = true;
            this.otherRadio.CheckedChanged += new System.EventHandler(this.otherRadio_CheckedChanged);
            // 
            // PeriodEditor
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(405, 334);
            this.Controls.Add(this.otherRadio);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.newGroupText);
            this.Controls.Add(this.newGroupBtn);
            this.Controls.Add(this.groupsListBox);
            this.Controls.Add(this.addPeriodBtn);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.endMText);
            this.Controls.Add(this.endHText);
            this.Controls.Add(this.startMText);
            this.Controls.Add(this.startHText);
            this.Controls.Add(this.nameLabel);
            this.Controls.Add(this.shortLabel);
            this.Controls.Add(this.periodName);
            this.Controls.Add(this.shortName);
            this.Controls.Add(this.periodN);
            this.Controls.Add(this.lunchRadio);
            this.Controls.Add(this.periodRadio);
            this.Name = "PeriodEditor";
            this.Text = "PeriodEditor";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.RadioButton periodRadio;
        private System.Windows.Forms.RadioButton lunchRadio;
        private System.Windows.Forms.TextBox periodN;
        private System.Windows.Forms.TextBox shortName;
        private System.Windows.Forms.TextBox periodName;
        private System.Windows.Forms.Label shortLabel;
        private System.Windows.Forms.Label nameLabel;
        private System.Windows.Forms.TextBox startHText;
        private System.Windows.Forms.TextBox startMText;
        private System.Windows.Forms.TextBox endMText;
        private System.Windows.Forms.TextBox endHText;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Button addPeriodBtn;
        private System.Windows.Forms.ListBox groupsListBox;
        private System.Windows.Forms.Button newGroupBtn;
        private System.Windows.Forms.TextBox newGroupText;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.RadioButton otherRadio;

    }
}