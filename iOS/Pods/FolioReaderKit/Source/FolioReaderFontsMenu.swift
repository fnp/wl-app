//
//  FolioReaderFontsMenu.swift
//  FolioReaderKit
//
//  Created by Heberti Almeida on 27/08/15.
//  Copyright (c) 2015 Folio Reader. All rights reserved.
//

import UIKit

public enum FolioReaderFont: Int {
    case andada = 0
    case lato
    case lora
    case raleway

    public static func folioReaderFont(fontName: String) -> FolioReaderFont? {
        var font: FolioReaderFont?
        switch fontName {
        case "andada": font = .andada
        case "lato": font = .lato
        case "lora": font = .lora
        case "raleway": font = .raleway
        default: break
        }
        return font
    }

    public var cssIdentifier: String {
        switch self {
        case .andada: return "andada"
        case .lato: return "lato"
        case .lora: return "lora"
        case .raleway: return "raleway"
        }
    }
}

//public enum FolioReaderFontSize: Int {
//    case xs = 0
//    case s
//    case m
//    case l
//    case xl
//
//    public static func folioReaderFontSize(fontSizeStringRepresentation: String) -> FolioReaderFontSize? {
//        var fontSize: FolioReaderFontSize?
//        switch fontSizeStringRepresentation {
//        case "textSizeOne": fontSize = .xs
//        case "textSizeTwo": fontSize = .s
//        case "textSizeThree": fontSize = .m
//        case "textSizeFour": fontSize = .l
//        case "textSizeFive": fontSize = .xl
//        default: break
//        }
//        return fontSize
//    }
//
//    public var cssIdentifier: String {
//        switch self {
//        case .xs: return "textSizeOne"
//        case .s: return "textSizeTwo"
//        case .m: return "textSizeThree"
//        case .l: return "textSizeFour"
//        case .xl: return "textSizeFive"
//        }
//    }
//}

//pd: changed
public enum SliderType: Int{
    case font
    case margin
    case interline
    
    var paramText: String{
        switch self {
        case .font:
            return "text"
        case .margin:
            return "margin"
        case .interline:
            return "interline"
        }
    }
    
    var leftImage: UIImage{
        switch self {
        case .font:
            return #imageLiteral(resourceName: "reader_font-small")
        case .margin:
            return #imageLiteral(resourceName: "reader_margin-small")
        case .interline:
            return #imageLiteral(resourceName: "reader_leading-small")
        }
    }
    
    var rightImage: UIImage{
        switch self {
        case .font:
            return #imageLiteral(resourceName: "reader_font-big")
        case .margin:
            return #imageLiteral(resourceName: "reader_margin-big")
        case .interline:
            return #imageLiteral(resourceName: "reader_leading-big")
        }
    }
}

public enum FolioReaderSliderParamSize: Int {
    case xs = 0
    case s
    case m
    case l
    case xl
    
    public static func folioReaderParamSize(fontSizeStringRepresentation: String, sliderType: SliderType) -> FolioReaderSliderParamSize? {
        var paramSize: FolioReaderSliderParamSize?
        let paramText = sliderType.paramText
        switch fontSizeStringRepresentation {
        case "\(paramText)SizeOne": paramSize = .xs
        case "\(paramText)SizeTwo": paramSize = .s
        case "\(paramText)SizeThree": paramSize = .m
        case "\(paramText)SizeFour": paramSize = .l
        case "\(paramText)SizeFive": paramSize = .xl
        default: break
        }
        return paramSize
    }
    
    public func cssIdentifier(sliderType: SliderType) -> String {
        let paramText = sliderType.paramText
        switch self {
        case .xs: return "\(paramText)SizeOne"
        case .s: return "\(paramText)SizeTwo"
        case .m: return "\(paramText)SizeThree"
        case .l: return "\(paramText)SizeFour"
        case .xl: return "\(paramText)SizeFive"
        }
    }
}
//pd: changed end

class FolioReaderFontsMenu: UIViewController, SMSegmentViewDelegate, UIGestureRecognizerDelegate {
    var menuView: UIView!

    fileprivate var readerConfig: FolioReaderConfig
    fileprivate var folioReader: FolioReader

    init(folioReader: FolioReader, readerConfig: FolioReaderConfig) {
        self.readerConfig = readerConfig
        self.folioReader = folioReader

        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.view.backgroundColor = UIColor.clear

        // Tap gesture
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(FolioReaderFontsMenu.tapGesture))
        tapGesture.numberOfTapsRequired = 1
        tapGesture.delegate = self
        view.addGestureRecognizer(tapGesture)

        // Menu view
//        let visibleHeight: CGFloat = self.readerConfig.canChangeScrollDirection ? 222 : 170
        //pd: added
        let visibleHeight: CGFloat = self.readerConfig.canChangeScrollDirection ? 222 + 114 : 170 + 114
        //pd: added end
        menuView = UIView(frame: CGRect(x: 0, y: view.frame.height-visibleHeight, width: view.frame.width, height: view.frame.height))
        menuView.backgroundColor = self.folioReader.isNight(self.readerConfig.nightModeMenuBackground, UIColor.white)
        menuView.autoresizingMask = .flexibleWidth
        menuView.layer.shadowColor = UIColor.black.cgColor
        menuView.layer.shadowOffset = CGSize(width: 0, height: 0)
        menuView.layer.shadowOpacity = 0.3
        menuView.layer.shadowRadius = 6
        menuView.layer.shadowPath = UIBezierPath(rect: menuView.bounds).cgPath
        menuView.layer.rasterizationScale = UIScreen.main.scale
        menuView.layer.shouldRasterize = true
        view.addSubview(menuView)

        let normalColor = UIColor(white: 0.5, alpha: 0.7)
        let selectedColor = self.readerConfig.tintColor
        let sun = UIImage(readerImageNamed: "icon-sun")
        let moon = UIImage(readerImageNamed: "icon-moon")
        let fontSmall = UIImage(readerImageNamed: "icon-font-small")
        let fontBig = UIImage(readerImageNamed: "icon-font-big")

        let sunNormal = sun?.imageTintColor(normalColor)?.withRenderingMode(.alwaysOriginal)
        let moonNormal = moon?.imageTintColor(normalColor)?.withRenderingMode(.alwaysOriginal)
        let fontSmallNormal = fontSmall?.imageTintColor(normalColor)?.withRenderingMode(.alwaysOriginal)
        let fontBigNormal = fontBig?.imageTintColor(normalColor)?.withRenderingMode(.alwaysOriginal)

        let sunSelected = sun?.imageTintColor(selectedColor)?.withRenderingMode(.alwaysOriginal)
        let moonSelected = moon?.imageTintColor(selectedColor)?.withRenderingMode(.alwaysOriginal)

        // Day night mode
        let dayNight = SMSegmentView(frame: CGRect(x: 0, y: 0, width: view.frame.width, height: 55),
                                     separatorColour: self.readerConfig.nightModeSeparatorColor,
                                     separatorWidth: 1,
                                     segmentProperties:  [
                                        keySegmentTitleFont: UIFont(name: "Avenir-Light", size: 17)!,
                                        keySegmentOnSelectionColour: UIColor.clear,
                                        keySegmentOffSelectionColour: UIColor.clear,
                                        keySegmentOnSelectionTextColour: selectedColor,
                                        keySegmentOffSelectionTextColour: normalColor,
                                        keyContentVerticalMargin: 17 as AnyObject
            ])
        dayNight.delegate = self
        dayNight.tag = 1
        dayNight.addSegmentWithTitle(self.readerConfig.localizedFontMenuDay, onSelectionImage: sunSelected, offSelectionImage: sunNormal)
        dayNight.addSegmentWithTitle(self.readerConfig.localizedFontMenuNight, onSelectionImage: moonSelected, offSelectionImage: moonNormal)
        dayNight.selectSegmentAtIndex(self.folioReader.nightMode ? 1 : 0)
        menuView.addSubview(dayNight)


        // Separator
        let line = UIView(frame: CGRect(x: 0, y: dayNight.frame.height+dayNight.frame.origin.y, width: view.frame.width, height: 1))
        line.backgroundColor = self.readerConfig.nightModeSeparatorColor
        menuView.addSubview(line)

        // Fonts adjust
        let fontName = SMSegmentView(frame: CGRect(x: 15, y: line.frame.height+line.frame.origin.y, width: view.frame.width-30, height: 55),
                                     separatorColour: UIColor.clear,
                                     separatorWidth: 0,
                                     segmentProperties:  [
                                        keySegmentOnSelectionColour: UIColor.clear,
                                        keySegmentOffSelectionColour: UIColor.clear,
                                        keySegmentOnSelectionTextColour: selectedColor,
                                        keySegmentOffSelectionTextColour: normalColor,
                                        keyContentVerticalMargin: 17 as AnyObject
            ])
        fontName.delegate = self
        fontName.tag = 2

        fontName.addSegmentWithTitle("Andada", onSelectionImage: nil, offSelectionImage: nil)
        fontName.addSegmentWithTitle("Lato", onSelectionImage: nil, offSelectionImage: nil)
        fontName.addSegmentWithTitle("Lora", onSelectionImage: nil, offSelectionImage: nil)
        fontName.addSegmentWithTitle("Raleway", onSelectionImage: nil, offSelectionImage: nil)

//        fontName.segments[0].titleFont = UIFont(name: "Andada-Regular", size: 18)!
//        fontName.segments[1].titleFont = UIFont(name: "Lato-Regular", size: 18)!
//        fontName.segments[2].titleFont = UIFont(name: "Lora-Regular", size: 18)!
//        fontName.segments[3].titleFont = UIFont(name: "Raleway-Regular", size: 18)!

        fontName.selectSegmentAtIndex(self.folioReader.currentFont.rawValue)
        menuView.addSubview(fontName)

        // Separator 2
        let line2 = UIView(frame: CGRect(x: 0, y: fontName.frame.height+fontName.frame.origin.y, width: view.frame.width, height: 1))
        line2.backgroundColor = self.readerConfig.nightModeSeparatorColor
        menuView.addSubview(line2)

        //pd added
        addSlider(sliderType: .font, topY: line2.frame.origin.y, selectedColor: selectedColor)
        
        addSlider(sliderType: .margin, topY: line2.frame.origin.y + 57, selectedColor: selectedColor)
        
        addSlider(sliderType: .interline, topY: line2.frame.origin.y + 114, selectedColor: selectedColor)
        //pd added end
        
//        // Font slider size
//        let slider = HADiscreteSlider(frame: CGRect(x: 60, y: line2.frame.origin.y+2, width: view.frame.width-120, height: 55))
//        slider.tickStyle = ComponentStyle.rounded
//        slider.tickCount = 5
//        slider.tickSize = CGSize(width: 8, height: 8)
//
//        slider.thumbStyle = ComponentStyle.rounded
//        slider.thumbSize = CGSize(width: 28, height: 28)
//        slider.thumbShadowOffset = CGSize(width: 0, height: 2)
//        slider.thumbShadowRadius = 3
//        slider.thumbColor = selectedColor
//
//        slider.backgroundColor = UIColor.clear
//        slider.tintColor = self.readerConfig.nightModeSeparatorColor
//        slider.minimumValue = 0
//        slider.value = CGFloat(self.folioReader.currentFontSize.rawValue)
//        slider.addTarget(self, action: #selector(FolioReaderFontsMenu.sliderValueChanged(_:)), for: UIControl.Event.valueChanged)
//
//        // Force remove fill color
//        slider.layer.sublayers?.forEach({ layer in
//            layer.backgroundColor = UIColor.clear.cgColor
//        })
//
//        menuView.addSubview(slider)
//
//        // Font icons
//        let fontSmallView = UIImageView(frame: CGRect(x: 20, y: line2.frame.origin.y+14, width: 30, height: 30))
//        fontSmallView.image = fontSmallNormal
//        fontSmallView.contentMode = UIView.ContentMode.center
//        menuView.addSubview(fontSmallView)
//
//        let fontBigView = UIImageView(frame: CGRect(x: view.frame.width-50, y: line2.frame.origin.y+14, width: 30, height: 30))
//        fontBigView.image = fontBigNormal
//        fontBigView.contentMode = UIView.ContentMode.center
//        menuView.addSubview(fontBigView)

        // Only continues if user can change scroll direction
        guard (self.readerConfig.canChangeScrollDirection == true) else {
            return
        }

        // Separator 3
//        let line3 = UIView(frame: CGRect(x: 0, y: line2.frame.origin.y+56, width: view.frame.width, height: 1))
        //pd: changed
        let line3 = UIView(frame: CGRect(x: 0, y: line2.frame.origin.y+171, width: view.frame.width, height: 1))
        //pd: changed end

        line3.backgroundColor = self.readerConfig.nightModeSeparatorColor
        menuView.addSubview(line3)

        let vertical = UIImage(readerImageNamed: "icon-menu-vertical")
        let horizontal = UIImage(readerImageNamed: "icon-menu-horizontal")
        let verticalNormal = vertical?.imageTintColor(normalColor)?.withRenderingMode(.alwaysOriginal)
        let horizontalNormal = horizontal?.imageTintColor(normalColor)?.withRenderingMode(.alwaysOriginal)
        let verticalSelected = vertical?.imageTintColor(selectedColor)?.withRenderingMode(.alwaysOriginal)
        let horizontalSelected = horizontal?.imageTintColor(selectedColor)?.withRenderingMode(.alwaysOriginal)

        // Layout direction
        let layoutDirection = SMSegmentView(frame: CGRect(x: 0, y: line3.frame.origin.y, width: view.frame.width, height: 55),
                                            separatorColour: self.readerConfig.nightModeSeparatorColor,
                                            separatorWidth: 1,
                                            segmentProperties:  [
                                                keySegmentTitleFont: UIFont(name: "Avenir-Light", size: 17)!,
                                                keySegmentOnSelectionColour: UIColor.clear,
                                                keySegmentOffSelectionColour: UIColor.clear,
                                                keySegmentOnSelectionTextColour: selectedColor,
                                                keySegmentOffSelectionTextColour: normalColor,
                                                keyContentVerticalMargin: 17 as AnyObject
            ])
        layoutDirection.delegate = self
        layoutDirection.tag = 3
        layoutDirection.addSegmentWithTitle(self.readerConfig.localizedLayoutVertical, onSelectionImage: verticalSelected, offSelectionImage: verticalNormal)
        layoutDirection.addSegmentWithTitle(self.readerConfig.localizedLayoutHorizontal, onSelectionImage: horizontalSelected, offSelectionImage: horizontalNormal)

        var scrollDirection = FolioReaderScrollDirection(rawValue: self.folioReader.currentScrollDirection)

        if scrollDirection == .defaultVertical && self.readerConfig.scrollDirection != .defaultVertical {
            scrollDirection = self.readerConfig.scrollDirection
        }

        switch scrollDirection ?? .vertical {
        case .vertical, .defaultVertical:
            layoutDirection.selectSegmentAtIndex(FolioReaderScrollDirection.vertical.rawValue)
        case .horizontal, .horizontalWithVerticalContent:
            layoutDirection.selectSegmentAtIndex(FolioReaderScrollDirection.horizontal.rawValue)
        }
        menuView.addSubview(layoutDirection)
    }
    //pd: added
    func addSlider(sliderType: SliderType, topY: CGFloat, selectedColor: UIColor){
        
        let normalColor = UIColor(white: 0.5, alpha: 0.7)
        
        // Font slider size
        let slider = HADiscreteSlider(frame: CGRect(x: 60, y: topY+2, width: view.frame.width-120, height: 55))
        slider.tickStyle = ComponentStyle.rounded
        slider.tickCount = 5
        slider.tickSize = CGSize(width: 8, height: 8)
        
        slider.thumbStyle = ComponentStyle.rounded
        slider.thumbSize = CGSize(width: 28, height: 28)
        slider.thumbShadowOffset = CGSize(width: 0, height: 2)
        slider.thumbShadowRadius = 3
        slider.thumbColor = selectedColor
        
        slider.backgroundColor = UIColor.clear
        slider.tintColor = self.readerConfig.nightModeSeparatorColor
        slider.minimumValue = 0
        slider.tag = sliderType.rawValue
        
        switch sliderType {
        case .font:
            slider.value = CGFloat(self.folioReader.currentFontSize.rawValue)
        case .margin:
            slider.value = CGFloat(self.folioReader.currentMarginSize.rawValue)
        case .interline:
            slider.value = CGFloat(self.folioReader.currentInterlineSize.rawValue)
        }
        
        slider.addTarget(self, action: #selector(FolioReaderFontsMenu.sliderValueChanged(_:)), for: UIControl.Event.valueChanged)
        
        // Force remove fill color
        slider.layer.sublayers?.forEach({ layer in
            layer.backgroundColor = UIColor.clear.cgColor
        })
        
        menuView.addSubview(slider)
        
        // Font icons
        let fontSmallView = UIImageView(frame: CGRect(x: 20, y: topY+14, width: 30, height: 30))
        fontSmallView.image = sliderType.leftImage.imageTintColor(normalColor)
        fontSmallView.contentMode = UIView.ContentMode.center
        menuView.addSubview(fontSmallView)
        
        let fontBigView = UIImageView(frame: CGRect(x: view.frame.width-50, y: topY+14, width: 30, height: 30))
        fontSmallView.image = sliderType.rightImage.imageTintColor(normalColor)
        fontBigView.contentMode = UIView.ContentMode.center
        menuView.addSubview(fontBigView)
    }
    //pd: added end
    
    // MARK: - SMSegmentView delegate

    func segmentView(_ segmentView: SMSegmentView, didSelectSegmentAtIndex index: Int) {
        guard (self.folioReader.readerCenter?.currentPage) != nil else { return }

        if segmentView.tag == 1 {

            self.folioReader.nightMode = Bool(index == 1)

            UIView.animate(withDuration: 0.6, animations: {
                self.menuView.backgroundColor = (self.folioReader.nightMode ? self.readerConfig.nightModeBackground : UIColor.white)
            })

        } else if segmentView.tag == 2 {

            self.folioReader.currentFont = FolioReaderFont(rawValue: index)!

        }  else if segmentView.tag == 3 {

            guard self.folioReader.currentScrollDirection != index else {
                return
            }

            self.folioReader.currentScrollDirection = index
        }
    }
    
    // MARK: - Font slider changed
    
//    @objc func sliderValueChanged(_ sender: HADiscreteSlider) {
//        guard
//            (self.folioReader.readerCenter?.currentPage != nil),
//            let fontSize = FolioReaderFontSize(rawValue: Int(sender.value)) else {
//                return
//        }
//
//        self.folioReader.currentFontSize = fontSize
//    }

    //pd: changed
    @objc func sliderValueChanged(_ sender: HADiscreteSlider) {
        guard
            (self.folioReader.readerCenter?.currentPage != nil), let sliderType = SliderType(rawValue: sender.tag)
            else {
                return
        }
        
        switch sliderType {
        case .font:
            if let fontSize = FolioReaderSliderParamSize(rawValue: Int(sender.value)){
                self.folioReader.currentFontSize = fontSize
            }
        case .margin:
            if let marginSize = FolioReaderSliderParamSize(rawValue: Int(sender.value)){
                self.folioReader.currentMarginSize = marginSize
            }
        case .interline:
            if let interlineSize = FolioReaderSliderParamSize(rawValue: Int(sender.value)){
                self.folioReader.currentInterlineSize = interlineSize
            }
        }
    }
    //pd: changed end
    // MARK: - Gestures
    
    @objc func tapGesture() {
        dismiss()
        
        if (self.readerConfig.shouldHideNavigationOnTap == false) {
            self.folioReader.readerCenter?.showBars()
        }
    }
    
    func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
        if gestureRecognizer is UITapGestureRecognizer && touch.view == view {
            return true
        }
        return false
    }
    
    // MARK: - Status Bar
    
    override var prefersStatusBarHidden : Bool {
        return (self.readerConfig.shouldHideNavigationOnTap == true)
    }
}
