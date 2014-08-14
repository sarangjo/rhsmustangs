using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI.Core;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkID=390556

namespace RHSMustangs
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class MyWebPage : Page
    {
        public MyWebPage()
        {
            this.InitializeComponent();
        }

        /// <summary>
        /// Invoked when this page is about to be displayed in a Frame.
        /// </summary>
        /// <param name="e">Event data that describes how this page was reached.
        /// This parameter is typically used to configure the page.</param>
        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
        }

        private void loadButton_Click(object sender, RoutedEventArgs e)
        {
            HttpWebRequest request = WebRequest.CreateHttp("http://www.google.com/");

            IAsyncResult result = (IAsyncResult)request.BeginGetResponse(ReadWebRequestCallback, request);
        }
        

        private async void ReadWebRequestCallback(IAsyncResult result)
        {
            HttpWebRequest mRequest = (HttpWebRequest)result.AsyncState;
            HttpWebResponse mResponse = (HttpWebResponse)mRequest.EndGetResponse(result);

            StreamReader reader = new StreamReader(mResponse.GetResponseStream());
            string page = reader.ReadToEnd();

            CoreDispatcher dispatch = CoreWindow.GetForCurrentThread().Dispatcher;
            await dispatch.RunAsync(CoreDispatcherPriority.Normal, () => { pageText.Text = page; });
        }
    }
}
