//
//  WLReaderConfig.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 25/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import FolioReaderKit

class WLReaderConfig: FolioReaderConfig {

    override init() {
        super.init()
        self.tintColor = Constants.Colors.lightGreenBgColor()
        self.displayTitle = true
        self.allowSharing = false
        self.enableTTS = false
        self.useReaderMenuController = false
    }
}
