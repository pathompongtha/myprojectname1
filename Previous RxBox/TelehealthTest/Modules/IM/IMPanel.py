#!/usr/bin/env python
# -*- coding: utf-8 -*-
# generated by wxGlade 0.6.3 on Thu Aug 26 20:47:46 2010

import wx

# begin wxGlade: extracode
# end wxGlade



class IMPanel(wx.Panel):
    def __init__(self, *args, **kwds):
        # begin wxGlade: IMPanel.__init__
        kwds["style"] = wx.TAB_TRAVERSAL
        wx.Panel.__init__(self, *args, **kwds)
        self.IMtexts_Text = wx.TextCtrl(self, -1, "", style=wx.TE_PROCESS_ENTER|wx.TE_MULTILINE|wx.TE_READONLY)
        self.IMreply_Text = wx.TextCtrl(self, -1, "", style=wx.TE_PROCESS_ENTER|wx.TE_MULTILINE)
        self.im_bottom_panel = wx.Panel(self, -1)

        self.__set_properties()
        self.__do_layout()
        # end wxGlade

    def __set_properties(self):
        # begin wxGlade: IMPanel.__set_properties
        self.SetBackgroundColour(wx.Colour(226, 255, 180))
        self.IMtexts_Text.SetBackgroundColour(wx.Colour(255, 255, 255))
        self.IMreply_Text.SetBackgroundColour(wx.Colour(255, 255, 255))
        self.im_bottom_panel.SetBackgroundColour(wx.Colour(226, 255, 180))
        # end wxGlade

    def __do_layout(self):
        # begin wxGlade: IMPanel.__do_layout
        im_main_sizer = wx.BoxSizer(wx.VERTICAL)
        im_text_main_sizer = wx.BoxSizer(wx.HORIZONTAL)
        im_text_sizer = wx.BoxSizer(wx.VERTICAL)
        im_text_sizer.Add(self.IMtexts_Text, 8, wx.TOP|wx.EXPAND, 1)
        im_text_sizer.Add(self.IMreply_Text, 2, wx.TOP|wx.EXPAND, 0)
        im_text_sizer.Add(self.im_bottom_panel, 0, wx.EXPAND, 0)
        im_text_main_sizer.Add(im_text_sizer, 8, wx.EXPAND, 0)
        im_main_sizer.Add(im_text_main_sizer, 1, wx.EXPAND, 0)
        self.SetSizer(im_main_sizer)
        im_main_sizer.Fit(self)
        # end wxGlade

# end of class IMPanel


