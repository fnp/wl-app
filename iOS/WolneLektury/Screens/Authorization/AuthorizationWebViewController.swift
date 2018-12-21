//
//  AuthorizationWebViewController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 14/07/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import OAuthSwift
typealias WebView = UIWebView // WKWebView

class AuthorizationWebViewController: OAuthWebViewController {
    
    var targetURL: URL?
    let webView: WebView = WebView()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.webView.frame = UIScreen.main.bounds
        self.webView.scalesPageToFit = true
        self.webView.delegate = self
        self.view.addSubview(self.webView)
        loadAddressURL()
    }
    
    override func handle(_ url: URL) {
        targetURL = url
        super.handle(url)
        self.loadAddressURL()
    }
    
    func loadAddressURL() {
        guard let url = targetURL else {
            return
        }
        let req = URLRequest(url: url)
        self.webView.loadRequest(req)
    }
    
    open override func doHandle(_ url: URL) {
        let completion: () -> Void = { [unowned self] in
            self.delegate?.oauthWebViewControllerDidPresent()
        }
        appDelegate.mainNavigator.setRootViewController(controller: self)        
    }
}

// MARK: delegate
extension AuthorizationWebViewController: UIWebViewDelegate {
    func webView(_ webView: UIWebView, shouldStartLoadWith request: URLRequest, navigationType: UIWebViewNavigationType) -> Bool {
        if let url = request.url, url.scheme == "oauth-swift" {
            // Call here AppDelegate.sharedInstance.applicationHandleOpenURL(url) if necessary ie. if AppDelegate not configured to handle URL scheme
            // compare the url with your own custom provided one in `authorizeWithCallbackURL`
            self.dismissWebViewController()
        }
        return true
    }
}
