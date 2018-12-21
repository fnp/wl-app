//
//  DateFormatter+Ext.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 30/05/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import Foundation

extension DateFormatter
{
    @nonobjc static let yyyy_mm_dd_dateFormatter: DateFormatter = {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        return dateFormatter
    }()
}
