namespace RHSMustangsPR
{
    partial class SchedulePR
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
            this.baseSchedBtn = new System.Windows.Forms.Button();
            this.updatedSchedBtn = new System.Windows.Forms.Button();
            this.label1 = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // baseSchedBtn
            // 
            this.baseSchedBtn.Location = new System.Drawing.Point(47, 47);
            this.baseSchedBtn.Name = "baseSchedBtn";
            this.baseSchedBtn.Size = new System.Drawing.Size(198, 89);
            this.baseSchedBtn.TabIndex = 0;
            this.baseSchedBtn.Text = "Base Schedule";
            this.baseSchedBtn.UseVisualStyleBackColor = true;
            this.baseSchedBtn.Click += new System.EventHandler(this.baseSchedBtn_Click);
            // 
            // updatedSchedBtn
            // 
            this.updatedSchedBtn.Location = new System.Drawing.Point(47, 142);
            this.updatedSchedBtn.Name = "updatedSchedBtn";
            this.updatedSchedBtn.Size = new System.Drawing.Size(198, 90);
            this.updatedSchedBtn.TabIndex = 1;
            this.updatedSchedBtn.Text = "Updates Schedule";
            this.updatedSchedBtn.UseVisualStyleBackColor = true;
            this.updatedSchedBtn.Click += new System.EventHandler(this.updatedSchedBtn_Click);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(103, 9);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(82, 13);
            this.label1.TabIndex = 2;
            this.label1.Text = "Schedule Editor";
            // 
            // SchedulePR
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(284, 261);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.updatedSchedBtn);
            this.Controls.Add(this.baseSchedBtn);
            this.Name = "SchedulePR";
            this.Text = "SchedulePR";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Button baseSchedBtn;
        private System.Windows.Forms.Button updatedSchedBtn;
        private System.Windows.Forms.Label label1;
    }
}