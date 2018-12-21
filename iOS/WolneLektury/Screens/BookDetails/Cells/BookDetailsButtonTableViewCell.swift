//
//  BookDetailsButtonTableViewCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 20/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit



enum BookDetailsButtonType{
    case download_ebook
    case download_ebook_loading
    case download_ebook_read
    case download_audiobook
    case download_audiobook_listen
    case download_audiobook_loading
    case support_us
    
    var color: UIColor{
        switch self{
        case .download_ebook, .download_ebook_loading, .download_ebook_read:
            return UIColor(red:0.91, green:0.31, blue:0.20, alpha:1.00)
        case .download_audiobook, .download_audiobook_listen, .download_audiobook_loading:
            return UIColor(red:0.00, green:0.53, blue:0.56, alpha:1.00)
        case .support_us:
            return UIColor(red:1.00, green:0.64, blue:0.26, alpha:1.00)
        }
    }
    
    func getTitle() -> String{
        return "\(self)".localized.uppercased()
    }
    
    var icon: UIImage{
        switch self {
        case .download_ebook, .download_ebook_loading, .download_ebook_read:
            return #imageLiteral(resourceName: "icon_glasses-mid")
        case .download_audiobook, .download_audiobook_listen, .download_audiobook_loading:
            return #imageLiteral(resourceName: "icon_speaker-mid")
        case .support_us:
            return #imageLiteral(resourceName: "icon_star-mid")
        }
    }
    
    var tintColor: UIColor{
        
        switch self {
        case .download_ebook_read, .download_audiobook_listen, .support_us:
            return UIColor.white
        default:
            return color
        }
    }
    
    var deleteButtonHidden: Bool{
        switch self {
        case .download_ebook, .download_audiobook, .support_us:
            return true
        default:
            return false
        }
    }
}

protocol BookDetailsButtonTableViewCellDelegate: class{
    func bookDetailsButtonTableViewCellButtonTapped(buttonType: BookDetailsButtonType)
    func bookDetailsButtonTableViewCellDeleteButtonTapped(buttonType: BookDetailsButtonType)
}

class BookDetailsButtonTableViewCell: WLTableViewCell {
    
    weak var delegate: BookDetailsButtonTableViewCellDelegate?
//    @IBOutlet weak var button: UIButton!
    @IBOutlet weak var button: ProgressLabel!
    @IBOutlet weak var buttonIcon: UIImageView!
    @IBOutlet weak var deleteButton: UIButton!
    var buttonType: BookDetailsButtonType!

    class func instance(delegate: BookDetailsButtonTableViewCellDelegate, bookDetailsButtonType: BookDetailsButtonType, bookDetailsModel: BookDetailsModel) -> BookDetailsButtonTableViewCell{
        let cell = BookDetailsButtonTableViewCell.instance(type: BookDetailsButtonTableViewCell.self)
        cell.delegate = delegate
        cell.setup(bookDetailsButtonType: bookDetailsButtonType, progress: nil, bookDetailsModel: bookDetailsModel)
        return cell
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        deleteButton.isHidden = true
//        button.layer.cornerRadius = 10
//        button.layer.borderWidth = 1
        button.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(buttonAction)))
    }

    func setup(bookDetailsButtonType: BookDetailsButtonType, progress:Float?, bookDetailsModel: BookDetailsModel?){
        self.buttonType = bookDetailsButtonType
        let tintColor = bookDetailsButtonType.tintColor
        button.color = bookDetailsButtonType.color
//        button.backgroundColor = bookDetailsButtonType.bgColor
//        button.layer.borderColor = bookDetailsButtonType.color.cgColor
        buttonIcon.tintColor = tintColor
        buttonIcon.image = bookDetailsButtonType.icon
        button.fullProgress = false
        if let progress = progress{
            button.progress = progress * 100
        }
        else {
            var customText = bookDetailsButtonType.getTitle()
            if bookDetailsButtonType == .download_audiobook_listen {
                button.fullProgress = true
                if let bookDetailsModel = bookDetailsModel, bookDetailsModel.totalAudioChapters > 0 {
                    customText += " \(bookDetailsModel.currentAudioChapter + 1)/\(bookDetailsModel.totalAudioChapters)"
                }
            }
            else if bookDetailsButtonType == .download_ebook_read {
                button.fullProgress = true
                if let bookDetailsModel = bookDetailsModel, bookDetailsModel.totalChapters > 0 {
                    customText += " \(bookDetailsModel.currentChapter)/\(bookDetailsModel.totalChapters)"
                }
            }

            button.customText = customText
        }
        
        deleteButton.isHidden = bookDetailsButtonType.deleteButtonHidden
    }
    
    @objc func buttonAction(_ sender: Any) {
        delegate?.bookDetailsButtonTableViewCellButtonTapped(buttonType: buttonType)
    }
    
    @IBAction func deleteButtonAction(_ sender: Any) {
        delegate?.bookDetailsButtonTableViewCellDeleteButtonTapped(buttonType: buttonType)
    }
    
    override func getHeight() -> CGFloat {
        return 55
    }
    
    deinit {
        print("deinit \(self)")
    }
}
