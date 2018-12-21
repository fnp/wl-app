//
//  PlayerItemTableViewCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 22/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class PlayerItemTableViewCell: UITableViewCell {

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var playingIcon: UIImageView!

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        selectionStyle = .none
        backgroundColor = .clear
        contentView.backgroundColor = .clear
    }
    
    func setup(mediaModel: MediaModel, isPlaying: Bool) {
        titleLabel.text = mediaModel.name
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        playingIcon.isHidden = !selected
    }
}
