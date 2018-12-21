//
//  MatomoTracker+Ext.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 25/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import Foundation
import MatomoTracker

extension MatomoTracker {
    static let shared: MatomoTracker = MatomoTracker(siteId: Config.PIWIK_SITE_ID, baseURL: URL(string: Config.PIWIK_URL)!)
}
