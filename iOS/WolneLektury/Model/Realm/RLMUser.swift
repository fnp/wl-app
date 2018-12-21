//
//  RLMUser.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 24/08/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import RealmSwift

class RLMUser: Object {
    @objc dynamic var username: String!
    @objc dynamic var premium: Bool = false

}
