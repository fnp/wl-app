//
//  NewsDetailsViewController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 15/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit

class NewsDetailsViewController: WLViewController {

    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var backButton: UIButton!
    @IBOutlet weak var headerView: UIView!
    @IBOutlet weak var titleLabel: UILabel!
    
    @IBOutlet weak var whenTitleLabel: UILabel!
    @IBOutlet weak var whenDescLabel: UILabel!
    @IBOutlet weak var whereTitleLabel: UILabel!
    @IBOutlet weak var whereDescLabel: UILabel!
    @IBOutlet weak var descLabel: UILabel!
    
    @IBOutlet weak var shareButton: UIButton!
    @IBOutlet weak var galleryCollectionView: UICollectionView!
    @IBOutlet weak var pageControl: UIPageControl!
    @IBOutlet weak var headerViewHeightLayoutConstraint: NSLayoutConstraint!

    private var flowLayout: UICollectionViewFlowLayout = UICollectionViewFlowLayout()
    
    var imageFullScreen = false
    var countDidLayoutSubviews = 0
    var collectionViewInitialized = false
    var newsModel: NewsModel!
    var smallHeaderHeight: CGFloat = 200
    
    open override var preferredStatusBarStyle : UIStatusBarStyle {
        return .lightContent
    }

    static func instance(newsModel: NewsModel) -> NewsDetailsViewController {
        let controller = NewsDetailsViewController.instance()
        controller.newsModel = newsModel
        return controller
    }

    public var currentPage: Int {
        get {
            return Int(galleryCollectionView.contentOffset.x / galleryCollectionView.frame.size.width)
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        
        title = newsModel.title
        smallHeaderHeight = 44 + UIApplication.shared.statusBarFrame.size.height
        updatePageControl()

        DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
            self.setupCollectionViewIfNeeded()
        }
        shareButton.layer.cornerRadius = 21
        shareButton.addDropShadow()
        
        titleLabel.text = newsModel.title
        whenTitleLabel.text = "news_when".localized
        whenDescLabel.text = newsModel.time
        whereTitleLabel.text = "news_where".localized
        whereDescLabel.text = newsModel.place
        
        if let htmlString = try? NSAttributedString(html: newsModel.body){
            descLabel.attributedText =  htmlString
        }
        else {
            descLabel.text = newsModel.body
        }
    }
    
    func updatePageControl()  {
        pageControl.currentPage = currentPage
    }

    private func setupCollectionViewIfNeeded(){
        if collectionViewInitialized == false{
            setupCollectionView()
        }
    }

    private func setupCollectionView(){// -> UICollectionView {
        // Set up flow layout
        collectionViewInitialized = true
        
        flowLayout.scrollDirection = UICollectionViewScrollDirection.horizontal
        flowLayout.minimumInteritemSpacing = 0
        flowLayout.minimumLineSpacing = 0
        
        let size = galleryCollectionView.bounds.size
        flowLayout.itemSize = size
        
        galleryCollectionView.collectionViewLayout = flowLayout
        galleryCollectionView.register(UINib (nibName: "SimpleGalleryCollectionViewCell", bundle: nil), forCellWithReuseIdentifier: "SimpleGalleryCollectionViewCell")
        galleryCollectionView.dataSource = self
        galleryCollectionView.delegate = self
        galleryCollectionView.backgroundColor = UIColor.black
        galleryCollectionView.isPagingEnabled = true
        
        galleryCollectionView.contentSize = CGSize(width: 1000.0, height: 1.0)
        
        galleryCollectionView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(NewsDetailsViewController.collectionViewTapped)))
    }

    @objc func collectionViewTapped() {
        guard newsModel.gallery_urls.count > 0 else{
            return
        }
        
        navigationController?.present(GalleryViewController.instance(photos: newsModel.gallery_urls,startIndex: currentPage), animated: true, completion: nil)
    }
    
    @IBAction func shareButtonAction(_ sender: UIButton) {
        if let url = newsModel?.url {

            self.share(string: url, button: sender)

        }
    }
}

extension NewsDetailsViewController: UICollectionViewDataSource {
    
    public func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    public func collectionView(_ imageCollectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return newsModel.gallery_urls.count
    }
    
    public func collectionView(_ imageCollectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        let cell = imageCollectionView.dequeueReusableCell(withReuseIdentifier: "SimpleGalleryCollectionViewCell", for: indexPath) as! SimpleGalleryCollectionViewCell
        cell.setup(imageUrlString: newsModel.gallery_urls[indexPath.row])
        return cell
    }
}

extension NewsDetailsViewController: UICollectionViewDelegate {
    
    public func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        
        // If the scroll animation ended, update the page control to reflect the current page we are on
        updatePageControl()
    }
}
