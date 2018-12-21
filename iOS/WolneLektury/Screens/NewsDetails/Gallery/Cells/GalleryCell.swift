//
//  GalleryCell.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 17/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import Kingfisher
import MBProgressHUD

public class GalleryCell: UICollectionViewCell {
    
    var url: String?{
        didSet {
            configureForNewImageUrl()
        }
    }
    
    private var scrollView: UIScrollView
    fileprivate let imageView: UIImageView
    var progressHud: MBProgressHUD?
    
    func setScrollEnabled(scrollEnabled: Bool){
        scrollView.isUserInteractionEnabled = scrollEnabled
        if !scrollEnabled{
            setZoomScale()
        }
    }
    
    override init(frame: CGRect) {
        
        imageView = UIImageView()
        imageView.translatesAutoresizingMaskIntoConstraints = false
        scrollView = UIScrollView(frame: frame)
        scrollView.translatesAutoresizingMaskIntoConstraints = false
        scrollView.backgroundColor = Constants.Colors.darkGreenBgColor()
        super.init(frame: frame)
        
        var scrollViewConstraints: [NSLayoutConstraint] = []
        var imageViewConstraints: [NSLayoutConstraint] = []
        
        scrollViewConstraints.append(NSLayoutConstraint(item: scrollView, attribute: .leading, relatedBy: .equal, toItem: contentView, attribute: .leading, multiplier: 1, constant: 0))
        scrollViewConstraints.append(NSLayoutConstraint(item: scrollView, attribute: .top, relatedBy: .equal, toItem: contentView, attribute: .top, multiplier: 1, constant: 0))
        scrollViewConstraints.append(NSLayoutConstraint(item: scrollView, attribute: .trailing, relatedBy: .equal, toItem: contentView, attribute: .trailing, multiplier: 1, constant: 0))
        scrollViewConstraints.append(NSLayoutConstraint(item: scrollView, attribute: .bottom, relatedBy: .equal, toItem: contentView, attribute: .bottom, multiplier: 1, constant: 0))
        contentView.addSubview(scrollView)
        contentView.addConstraints(scrollViewConstraints)
        
        imageViewConstraints.append(NSLayoutConstraint(item: imageView, attribute: .leading, relatedBy: .equal, toItem: scrollView, attribute: .leading, multiplier: 1, constant: 0))
        imageViewConstraints.append(NSLayoutConstraint(item: imageView, attribute: .top, relatedBy: .equal, toItem: scrollView, attribute: .top, multiplier: 1, constant: 0))
        imageViewConstraints.append(NSLayoutConstraint(item: imageView, attribute: .trailing, relatedBy: .equal, toItem: scrollView, attribute: .trailing, multiplier: 1, constant: 0))
        imageViewConstraints.append(NSLayoutConstraint(item: imageView, attribute: .bottom, relatedBy: .equal, toItem: scrollView, attribute: .bottom, multiplier: 1, constant: 0))
        scrollView.addSubview(imageView)
        scrollView.addConstraints(imageViewConstraints)

        scrollView.delegate = self
        
        let tapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(tapGestureRecognizerAction(recognizer:)))
        tapGestureRecognizer.numberOfTapsRequired = 2
        self.addGestureRecognizer(tapGestureRecognizer)
    }
    
    @objc public func tapGestureRecognizerAction(recognizer: UITapGestureRecognizer) {
        if (scrollView.zoomScale > scrollView.minimumZoomScale) {
            scrollView.setZoomScale(scrollView.minimumZoomScale, animated: true)
        } else {
            scrollView.setZoomScale(scrollView.maximumZoomScale, animated: true)
        }
    }

    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    public func configureForNewImageUrl() {
        
        guard let imageUrl = url?.getPhotoUrl() else{
            self.setupImage(image: nil)
            return
        }
        
        if progressHud == nil{
            progressHud = MBProgressHUD.showAdded(to: contentView, animated: true)
            progressHud?.label.text = "loading".localized
        }
        else{
            progressHud?.show(animated: true)
        }
        
        //        let freeSpace = contentView.bounds.size.height - textContainerHeight
        let contentOffsetY = ((contentView.bounds.size.height/2 /*- freeSpace/2*/) * -1)
        progressHud!.offset.y = contentOffsetY
        
        KingfisherManager.shared.retrieveImage(with: ImageResource(downloadURL: imageUrl), options: nil, progressBlock: nil) { (image, error, cacheType, url) in
            self.progressHud?.hide(animated: true)
            self.setupImage(image: image)
        }
    }

    
    // ############################################
    // MARK: - Private
    // ############################################
    
    private func setupImage(image: UIImage?){
        imageView.image = image
        imageView.sizeToFit()
        imageView.alpha = 0.0
        
        setZoomScale()
        scrollViewDidZoom(scrollView)
        
        UIView.animate(withDuration: 0.5) {
            self.imageView.alpha = 1.0
        }
    }
    
    private func setZoomScale() {
        
        let imageViewSize = imageView.bounds.size
        
        guard imageViewSize.width > 0 && imageViewSize.height > 0 else {
            return
        }
        
        let scrollViewSize =  UIScreen.main.bounds.size// scrollView.bounds.size
        let widthScale = scrollViewSize.width / imageViewSize.width
        let heightScale = scrollViewSize.height / imageViewSize.height
        let miminumZoomScale = min(widthScale, heightScale)
        scrollView.minimumZoomScale = miminumZoomScale
        
        let initialZoomScale = max(widthScale, heightScale)
        
        scrollView.maximumZoomScale = initialZoomScale * 1.5
        
        scrollView.setZoomScale(miminumZoomScale, animated: false)
        
        let newContentOffsetX = (scrollView.contentSize.width/2) - (scrollView.bounds.size.width/2);
        
        scrollView.contentOffset = CGPoint(x: newContentOffsetX, y: scrollView.contentOffset.y)
    }
}

extension GalleryCell: UIScrollViewDelegate {
    
    public func viewForZooming(in scrollView: UIScrollView) -> UIView? {
        return imageView
    }
    
    public func scrollViewDidZoom(_ scrollView: UIScrollView) {
        
        let imageViewSize = imageView.frame.size
        let scrollViewSize = scrollView.bounds.size
        
        let verticalPadding = imageViewSize.height < scrollViewSize.height ? (scrollViewSize.height - imageViewSize.height) / 2 : 0
        let horizontalPadding = imageViewSize.width < scrollViewSize.width ? (scrollViewSize.width - imageViewSize.width) / 2 : 0
        
        if verticalPadding >= 0 {
            // Center the image on screen
            scrollView.contentInset = UIEdgeInsets(top: verticalPadding, left: horizontalPadding, bottom: verticalPadding, right: horizontalPadding)
        } else {
            // Limit the image panning to the screen bounds
            scrollView.contentSize = imageViewSize
        }
    }
}
