# -*- coding: utf-8 -*-
"""
Created on Sat Apr 21 23:55:44 2018

@author: kelly
"""
"""
main will be executing encrypt/decrypt in accordance to user input
this program references CECS378 lab assignments and cryptography.io
"""
#note: python 3 uses tkinter instead of Tkinter (capital "T")
import tkinter
from tkinter import filedialog
from collections import namedtuple
import sys

def main():
    #create menu for ease of use
    MenuEntry = namedtuple('MenuEntry', ['index', 'description'])
    _menu = []
    _menu.append(MenuEntry(1, 'Encrypt a single File'))
    _menu.append(MenuEntry(2, 'Encrypt a full directory'))
    _menu.append(MenuEntry(3, 'Decrypt a single File'))
    _menu.append(MenuEntry(4, 'Decrypt a full directory'))
    _menu.append(MenuEntry(5, 'exit'))
    
    print("-------------Menu-------------")
    for entry in _menu:
            index = str(getattr(entry,'index')).ljust(5)
            descr = getattr(entry,'description').ljust(25)
            print ('{0}{1}'.format(index,descr))
    print("------------------------------")
    
    #prompt user for input
    choice = input("\nPlease select an item\n")
    
    #create tkinter instance
    root = tkinter.Tk()
    root.withdraw()
    
    #validate user input as an integer between 1 and 5    
    try:
        val = int(choice)
    except ValueError:
        print("Please input an integer between 1 and 5")
    else: 
        if choice >= 1 and choice <= 5:
            if choice == 1:
                root.update()
                chosenFilePath = filedialog.askopenfilename()
                root.destroy()
                if len(chosenFilePath) > 0:
                    print ("You chose %s" % chosenFilePath)
                else:
                    print("No file chosen")
            elif choice == 2:
                root.update()
                chosenDirPath = filedialog.askdirectory()
                root.destroy()
                if len(chosenDirPath) > 0:
                    print ("You chose %s" % chosenDirPath)
                else:
                    print("No Directory chosen")
            elif choice == 3:
                root.update()
                chosenFilePath = filedialog.askopenfilename()
                root.destroy()
                if len(chosenFilePath) > 0:
                    print ("You chose %s" % chosenFilePath)
                else:
                    print("No file chosen")
            elif choice == 4:
                root.update()
                chosenDirPath = filedialog.askdirectory()
                root.destroy()
                if len(chosenDirPath) > 0:
                    print ("You chose %s" % chosenDirPath)
                else:
                    print("No Directory chosen")
            elif choice == 5:
                print("terminating program")
                sys.exit()
        else:
            print("Please input an integer between 1 and 5")
    
    return 0