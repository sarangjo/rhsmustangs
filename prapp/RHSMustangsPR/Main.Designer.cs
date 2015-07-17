namespace RHSMustangsPR
{
    partial class Main
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
            this.addSchedule = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // addSchedule
            // 
            this.addSchedule.Location = new System.Drawing.Point(12, 12);
            this.addSchedule.Name = "addSchedule";
            this.addSchedule.Size = new System.Drawing.Size(260, 23);
            this.addSchedule.TabIndex = 0;
            this.addSchedule.Text = "Add Schedule";
            this.addSchedule.UseVisualStyleBackColor = true;
            this.addSchedule.Click += new System.EventHandler(this.addSchedule_Click);
            // 
            // Main
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(284, 261);
            this.Controls.Add(this.addSchedule);
            this.Name = "Main";
            this.Text = "RHS Schedule Editor";
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Button addSchedule;
    }
}

