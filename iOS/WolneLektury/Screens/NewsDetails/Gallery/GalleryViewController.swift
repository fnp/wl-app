//
//  GalleryViewController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 17/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class GalleryViewController: WLViewController {
    
    @IBOutlet weak var closeButton: UIButton!
    
    @IBOutlet weak var galleryCollectionView: UICollectionView!
    
    private var flowLayout: UICollectionViewFlowLayout = UICollectionViewFlowLayout()
    
    var dataSource: [String]!
    
    var countDidLayoutSubviews = 0
    var collectionViewInitialized = false
    var startIndex: Int!
    
    class func instance(photos: [String], startIndex: Int) -> GalleryViewController{
        
        let controller = GalleryViewController.instance()
        controller.dataSource = photos
        controller.startIndex = startIndex
        return controller
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        closeButton.tintColor = UIColor.white
        closeButton.accessibilityLabel = "close".localized
        closeButton.accessibilityHint = "close".localized
    }
    
    @IBAction func closeButtonAction(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        
        countDidLayoutSubviews += 1
        
        if countDidLayoutSubviews == 2 && collectionViewInitialized == false{
            setupCollectionViewIfNeeded()
        }
    }
    
    private func setupCollectionViewIfNeeded(){
        if collectionViewInitialized == false{
            setupCollectionView()
        }
    }
    
    open override var preferredStatusBarStyle : UIStatusBarStyle {
        return .lightContent
    }

    private func setupCollectionView(){// -> UICollectionView {
        // Set up flow layout
        collectionViewInitialized = true
        
        flowLayout.scrollDirection = UICollectionViewScrollDirection.horizontal
        flowLayout.minimumInteritemSpacing = 0
        flowLayout.minimumLineSpacing = 0
        
        flowLayout.itemSize = galleryCollectionView.bounds.size
        
        galleryCollectionView.collectionViewLayout = flowLayout
        
        galleryCollectionView.register(GalleryCell.self, forCellWithReuseIdentifier: "GalleryCell")
        galleryCollectionView.dataSource = self
        galleryCollectionView.delegate = self
        galleryCollectionView.backgroundColor = UIColor.black
        galleryCollectionView.isPagingEnabled = true
        
        galleryCollectionView.contentSize = CGSize(width: 1000.0, height: 1.0)
        
        galleryCollectionView.scrollToItem(
            at: IndexPath(row: startIndex, section: 0),
            at: [],
            animated: false)
    }
}

extension GalleryViewController: UICollectionViewDataSource {
    
    public func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    public func collectionView(_ imageCollectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return dataSource.count
    }
    
    public func collectionView(_ imageCollectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        let cell = imageCollectionView.dequeueReusableCell(withReuseIdentifier: "GalleryCell", for: indexPath) as! GalleryCell
        cell.url = dataSource[indexPath.row]
        cell.setScrollEnabled(scrollEnabled: true)
        return cell
    }
}

// MARK: UICollectionViewDelegate Methods
extension GalleryViewController: UICollectionViewDelegate {
    
    public func collectionView(_ collectionView: UICollectionView, willDisplay cell: UICollectionViewCell, forItemAt indexPath: IndexPath) {
        if let cell = cell as? GalleryCell {
            cell.configureForNewImageUrl()
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        
    }
    
    public func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        
        // If the scroll animation ended, update the page control to reflect the current page we are on
        //        updatePageControl()
    }
}
