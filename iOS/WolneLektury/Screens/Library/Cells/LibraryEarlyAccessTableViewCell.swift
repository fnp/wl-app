//
//  LibraryEarlyAccessTableViewCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 18/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import Kingfisher

protocol LibraryEarlyAccessTableViewCellDelegate: class {
    func libraryEarlyAccessTableViewCellRefreshButtonTapped()
}

class LibraryEarlyAccessTableViewCell: WLTableViewCell {
    var delegate: LibraryEarlyAccessTableViewCellDelegate?

    class func instance() -> LibraryEarlyAccessTableViewCell{
        let cell = LibraryEarlyAccessTableViewCell.instance(type: LibraryEarlyAccessTableViewCell.self)
        return cell
    }
    
    @IBOutlet weak var containerView: UIView!
    @IBOutlet weak var bookDescriptionView: BookDescriptionView!
    @IBOutlet weak var miniatureImageView: UIImageView!
    @IBOutlet weak var miniatureOverlayView: BookImageOverlayView!
    @IBOutlet weak var miniatureBgView: UIView!
    @IBOutlet weak var refreshButton: ActivityIndicatorButton!
    var book: BookDetailsModel?
    @IBOutlet weak var noPremiumBookLabel: UILabel!
    private var height: CGFloat = 199

    override func awakeFromNib() {
        super.awakeFromNib()
        miniatureBgView.layer.cornerRadius = 5
        noPremiumBookLabel.text = DatabaseManager.shared.isUserPremium() ? "library_empty_header_logged".localized : "library_empty_header".localized
    }
    
    func setup(state: ActivityIndicatorButtonState, bookDetailsModel: BookDetailsModel?) {
        
        self.book = bookDetailsModel
        
        noPremiumBookLabel.isHidden = true
        containerView.isHidden = true
        refreshButton.setIndicatorButtonState(state: state)
        height = 199
        switch state{
        case .hidden:
            if let book = self.book {
                containerView.isHidden = false
                bookDescriptionView.setup(bookDetailsModel: book, isPremium: true)
                miniatureOverlayView.hideTextAndIcons()
                miniatureImageView.kf.cancelDownloadTask()
                miniatureImageView.image = #imageLiteral(resourceName: "list_nocover")
                
                if let url = book.getCoverThumbUrl() {
                    
                    miniatureImageView.kf.setImage(with: ImageResource(downloadURL: url),
                                               placeholder: #imageLiteral(resourceName: "list_nocover"),
                                               options: [.transition(.fade(1))],
                                               progressBlock: nil,
                                               completionHandler: { (image, error, cacheType, url) in
                    })
                }
            }
            else {
                noPremiumBookLabel.isHidden = false
                height = 100
            }
        case .button, .loading:
            break
        }
    }
    
    @IBAction func refreshButtonAction(_ sender: Any) {
        delegate?.libraryEarlyAccessTableViewCellRefreshButtonTapped()
    }
    
    override func getHeight() -> CGFloat {

        return height
    }
}
