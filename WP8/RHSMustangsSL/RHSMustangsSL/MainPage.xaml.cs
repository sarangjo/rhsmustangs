using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;

namespace RHSMustangsSL
{
    public partial class LinksPage : PhoneApplicationPage
    {
        public LinksPage()
        {
            InitializeComponent();
        }

        private void linksButton_Tap(object sender, System.Windows.Input.GestureEventArgs e)
        {
            NavigationService.Navigate(new Uri("/LinksPage.xaml", UriKind.Relative));
        }
    }
}