#!/usr/bin/env python
# -*- coding: iso-8859-15 -*-
# generated by wxGlade 0.6.3 on Thu Jul  8 17:35:52 2010

import wx

# begin wxGlade: extracode
# end wxGlade



class Lead12Panel(wx.Panel):
    def __init__(self, *args, **kwds):
        # begin wxGlade: Lead12Panel.__init__
        kwds["style"] = wx.TAB_TRAVERSAL
        wx.Panel.__init__(self, *args, **kwds)
        self.aVR_sizer_staticbox = wx.StaticBox(self, -1, "aVR")
        self.V1_sizer_staticbox = wx.StaticBox(self, -1, "V1")
        self.V4_sizer_staticbox = wx.StaticBox(self, -1, "V4")
        self.small_leadII_sizer_staticbox = wx.StaticBox(self, -1, "II")
        self.aVL_sizer_staticbox = wx.StaticBox(self, -1, "aVL")
        self.V2_sizer_staticbox = wx.StaticBox(self, -1, "V2")
        self.V5_sizer_staticbox = wx.StaticBox(self, -1, "V5")
        self.leadIII_sizer_staticbox = wx.StaticBox(self, -1, "III")
        self.aVF_sizer_staticbox = wx.StaticBox(self, -1, "aVF")
        self.V3_sizer_staticbox = wx.StaticBox(self, -1, "V3")
        self.V6_sizer_staticbox = wx.StaticBox(self, -1, "V6")
        self.leadII_sizer_staticbox = wx.StaticBox(self, -1, "II")
        self.leadI_sizer_staticbox = wx.StaticBox(self, -1, "I")
        self.plotter_I = wx.Panel(self, -1)
        self.plotter_aVR = wx.Panel(self, -1)
        self.plotter_V1 = wx.Panel(self, -1)
        self.plotter_V4 = wx.Panel(self, -1)
        self.plotter_II = wx.Panel(self, -1)
        self.plotter_aVL = wx.Panel(self, -1)
        self.plotter_V2 = wx.Panel(self, -1)
        self.plotter_V5 = wx.Panel(self, -1)
        self.plotter_III = wx.Panel(self, -1)
        self.plotter_aVF = wx.Panel(self, -1)
        self.plotter_V3 = wx.Panel(self, -1)
        self.plotter_V6 = wx.Panel(self, -1)
        self.plotter_bigII = wx.Panel(self, -1)

        self.__set_properties()
        self.__do_layout()
        # end wxGlade

    def __set_properties(self):
        # begin wxGlade: Lead12Panel.__set_properties
        pass
        # end wxGlade

    def __do_layout(self):
        # begin wxGlade: Lead12Panel.__do_layout
        main_vertical_sizer = wx.BoxSizer(wx.VERTICAL)
        fourth_row_sizer = wx.BoxSizer(wx.HORIZONTAL)
        leadII_sizer = wx.StaticBoxSizer(self.leadII_sizer_staticbox, wx.VERTICAL)
        third_row_sizer = wx.BoxSizer(wx.HORIZONTAL)
        V6_sizer = wx.StaticBoxSizer(self.V6_sizer_staticbox, wx.VERTICAL)
        V3_sizer = wx.StaticBoxSizer(self.V3_sizer_staticbox, wx.VERTICAL)
        aVF_sizer = wx.StaticBoxSizer(self.aVF_sizer_staticbox, wx.VERTICAL)
        leadIII_sizer = wx.StaticBoxSizer(self.leadIII_sizer_staticbox, wx.VERTICAL)
        second_row_sizer = wx.BoxSizer(wx.HORIZONTAL)
        V5_sizer = wx.StaticBoxSizer(self.V5_sizer_staticbox, wx.VERTICAL)
        V2_sizer = wx.StaticBoxSizer(self.V2_sizer_staticbox, wx.VERTICAL)
        aVL_sizer = wx.StaticBoxSizer(self.aVL_sizer_staticbox, wx.VERTICAL)
        small_leadII_sizer = wx.StaticBoxSizer(self.small_leadII_sizer_staticbox, wx.VERTICAL)
        first_row_sizer = wx.BoxSizer(wx.HORIZONTAL)
        V4_sizer = wx.StaticBoxSizer(self.V4_sizer_staticbox, wx.VERTICAL)
        V1_sizer = wx.StaticBoxSizer(self.V1_sizer_staticbox, wx.VERTICAL)
        aVR_sizer = wx.StaticBoxSizer(self.aVR_sizer_staticbox, wx.VERTICAL)
        leadI_sizer = wx.StaticBoxSizer(self.leadI_sizer_staticbox, wx.VERTICAL)
        leadI_sizer.Add(self.plotter_I, 1, wx.EXPAND, 0)
        first_row_sizer.Add(leadI_sizer, 1, wx.EXPAND, 0)
        aVR_sizer.Add(self.plotter_aVR, 1, wx.EXPAND, 0)
        first_row_sizer.Add(aVR_sizer, 1, wx.EXPAND, 0)
        V1_sizer.Add(self.plotter_V1, 1, wx.EXPAND, 0)
        first_row_sizer.Add(V1_sizer, 1, wx.EXPAND, 0)
        V4_sizer.Add(self.plotter_V4, 1, wx.EXPAND, 0)
        first_row_sizer.Add(V4_sizer, 1, wx.EXPAND, 0)
        main_vertical_sizer.Add(first_row_sizer, 1, wx.EXPAND, 0)
        small_leadII_sizer.Add(self.plotter_II, 1, wx.EXPAND, 0)
        second_row_sizer.Add(small_leadII_sizer, 1, wx.EXPAND, 0)
        aVL_sizer.Add(self.plotter_aVL, 1, wx.EXPAND, 0)
        second_row_sizer.Add(aVL_sizer, 1, wx.EXPAND, 0)
        V2_sizer.Add(self.plotter_V2, 1, wx.EXPAND, 0)
        second_row_sizer.Add(V2_sizer, 1, wx.EXPAND, 0)
        V5_sizer.Add(self.plotter_V5, 1, wx.EXPAND, 0)
        second_row_sizer.Add(V5_sizer, 1, wx.EXPAND, 0)
        main_vertical_sizer.Add(second_row_sizer, 1, wx.EXPAND, 0)
        leadIII_sizer.Add(self.plotter_III, 1, wx.EXPAND, 0)
        third_row_sizer.Add(leadIII_sizer, 1, wx.EXPAND, 0)
        aVF_sizer.Add(self.plotter_aVF, 1, wx.EXPAND, 0)
        third_row_sizer.Add(aVF_sizer, 1, wx.EXPAND, 0)
        V3_sizer.Add(self.plotter_V3, 1, wx.EXPAND, 0)
        third_row_sizer.Add(V3_sizer, 1, wx.EXPAND, 0)
        V6_sizer.Add(self.plotter_V6, 1, wx.EXPAND, 0)
        third_row_sizer.Add(V6_sizer, 1, wx.EXPAND, 0)
        main_vertical_sizer.Add(third_row_sizer, 1, wx.EXPAND, 0)
        leadII_sizer.Add(self.plotter_bigII, 1, wx.EXPAND, 0)
        fourth_row_sizer.Add(leadII_sizer, 1, wx.EXPAND, 0)
        main_vertical_sizer.Add(fourth_row_sizer, 1, wx.EXPAND, 0)
        self.SetSizer(main_vertical_sizer)
        main_vertical_sizer.Fit(self)
        # end wxGlade

# end of class Lead12Panel


if __name__ == "__main__":
    app = wx.PySimpleApp(0)
    wx.InitAllImageHandlers()
    dialog_1 = (None, -1, "")
    app.SetTopWindow(dialog_1)
    dialog_1.Show()
    app.MainLoop()
