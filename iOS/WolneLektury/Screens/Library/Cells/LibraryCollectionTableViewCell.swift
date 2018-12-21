//
//  LibraryCollectionTableViewCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 18/06/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

enum LibraryCollectionType{
    case reading_now
    case newest
    case recommended
    
    var title: String{
        return "\(self)".localized.uppercased()
    }
    
    var noDataTitle: String{
        switch self {
        case .reading_now:
            return "read_now_library_empty".localized
        default:
            return ""
        }
    }
}

protocol LibraryCollectionTableViewCellDelegate: class {
    func libraryCollectionTableViewCellDelegateRefreshButtonTapped(collectionViewType: LibraryCollectionType)
    func libraryCollectionTableViewCellDelegateShowAllButtonTapped(collectionViewType: LibraryCollectionType)
    func libraryCollectionTableViewCellDelegateDidSelect(bookModel: BookModel)

}

class LibraryCollectionTableViewCell: WLTableViewCell {
    var delegate: LibraryCollectionTableViewCellDelegate?
    
    class func instance(libraryCollectionType: LibraryCollectionType) -> LibraryCollectionTableViewCell{
        let cell = LibraryCollectionTableViewCell.instance(type: LibraryCollectionTableViewCell.self)
        cell.libraryCollectionType = libraryCollectionType
        cell.titleLabel.text = libraryCollectionType.title
        cell.noDataLabel.text = libraryCollectionType.noDataTitle
        return cell
    }
    
    var libraryCollectionType: LibraryCollectionType!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var showAllButton: UIButton!
    @IBOutlet weak var collectionView: UICollectionView!
    @IBOutlet weak var refreshButton: ActivityIndicatorButton!
    @IBOutlet weak var showAllArrowImageView: UIImageView!
    @IBOutlet weak var noDataLabel: UILabel!
    
    var dataSource = [BookModel]()
    
    override func awakeFromNib() {
        super.awakeFromNib()
        showAllButton.text = "see_all".localized
        
        collectionView.register(UINib(nibName: "BookCollectionViewCell", bundle: nil), forCellWithReuseIdentifier: "BookCollectionViewCell")
        collectionView.delegate = self
        collectionView.dataSource = self
        showAllArrowImageView.tintColor = Constants.Colors.grayTextColor()
        noDataLabel.isHidden = true
    }
    
    @IBAction func refreshButtonAction(_ sender: Any) {
        delegate?.libraryCollectionTableViewCellDelegateRefreshButtonTapped(collectionViewType: libraryCollectionType)
    }
    
    @IBAction func showAllButtonAction(_ sender: Any) {
        delegate?.libraryCollectionTableViewCellDelegateShowAllButtonTapped(collectionViewType: libraryCollectionType)
    }
    
    func setup(state: ActivityIndicatorButtonState, dataSource: [BookModel]?) {
        
        refreshButton.setIndicatorButtonState(state: state)
        switch state{
        case .hidden:
            self.dataSource = dataSource ?? [BookModel]()
            collectionView.reloadData()
            collectionView.isHidden = false
            noDataLabel.isHidden = self.dataSource.count > 0
        case .button, .loading:
            collectionView.isHidden = true
        }
    }
    
    override func getHeight() -> CGFloat {
        return 221
    }
}

extension LibraryCollectionTableViewCell: UICollectionViewDataSource{
    public func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int{
        
        return dataSource.count
    }
    
    // The cell that is returned must be retrieved from a call to -dequeueReusableCellWithReuseIdentifier:forIndexPath:
    public func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell{
        
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "BookCollectionViewCell", for: indexPath) as! BookCollectionViewCell
        
        cell.setup(bookModel: dataSource[indexPath.row])
        
        return cell
    }
}

extension LibraryCollectionTableViewCell: UICollectionViewDelegate{
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        delegate?.libraryCollectionTableViewCellDelegateDidSelect(bookModel: dataSource[indexPath.row])
    }
}


