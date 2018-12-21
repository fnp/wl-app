//
//  ReadingStateModel.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 29/08/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class ReadingStateModel: Decodable{
    
    public enum ReadingState : String {
        case unknown        = "unknown"
        case not_started    = "not_started"
        case reading        = "reading"
        case complete       = "complete"
    }
    
    var state = ReadingState.unknown
    
    private enum ReadingStateModelCodingKeys: String, CodingKey {
        case state
    }

    convenience required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: ReadingStateModelCodingKeys.self)
        
        let stateString = try container.decode(String.self, forKey: .state)
        
        var returnState: ReadingState = .unknown
        
        if let readingState  = ReadingState(rawValue: stateString) {
            returnState = readingState
        }
        
        self.init(state: returnState)
    }
    
    convenience init(state: ReadingState) {
        self.init()
        self.state = state
    }
}
