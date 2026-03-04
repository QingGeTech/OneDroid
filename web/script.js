// 翻译数据
const translations = {
    zh: {
        features: '特性',
        download: '下载',
        screenshots: '截图',
        disclaimer: '免责声明',
        license: '许可协议',
        contributing: '贡献',
        contact: '联系我们',
        'hero-title': 'OneDroid - 系统级Android全能工具箱',
        'hero-subtitle': '一款功能强大的安卓多功能工具集，集成了多种实用工具，帮助用户更好地管理和了解自己的安卓设备。',
        'hero-btn': '立即下载',
        'features-title': '特性',
        'app-management': '应用管理',
        'app-list': '应用列表',
        'app-info': '基础信息、签名、权限、组件',
        'app-operations': '应用操作（启动、提取、卸载、修改内部文件、反编译）',
        'device-management': '设备管理',
        'device-info': '设备、系统、CPU、GPU、内存、存储、屏幕、网络、位置、传感器、相机、温度、PROP',
        'tool-collection': '小工具合集',
        'screen-analysis': '屏幕分析：长截图(待完善)、界面分析、屏幕录制、屏幕取色、屏幕取字',
        'reverse-debugging': '逆向调试：反编译、Logcat(待实现)、终端(待实现)、模拟位置(待实现)、wifi密码(需要root)',
        'network-management': '网络管理：抓包(待实现)、文件服务器(待实现)',
        'download-title': '下载',
        'download-desc': '在 Release页面 下载最新的APK',
        'download-btn': '前往下载',
        'screenshots-title': '截图展示',
        'app-management-screenshots': '应用管理',
        'basic-info': '基础信息',
        'device-management-screenshots': '设备管理 & 小工具合集',
        'disclaimer-title': '免责声明',
        'disclaimer-content': '本工具仅供学习与技术交流使用，请勿将其用于任何非法用途，使用该软件产生的任何法律后果由使用者自行承担。',
        'user-agreement': '用户协议',
        'privacy-policy': '隐私政策',
        'license-title': '许可协议',
        'license-content': '本项目采用 CC BY-NC-SA 4.0 (署名-非商业性使用-相同方式共享 4.0 国际) 许可协议。',
        'core-terms': '核心条款：',
        'attribution': '署名 (Attribution)：您必须给出适当的署名，并提供指向本许可协议的链接。',
        'non-commercial': '非商业性使用 (Non-Commercial)：您不得将本作品用于任何形式的商业目的（包括但不限于：直接出售、在应用内加入广告、用于付费课程、或作为收费服务的组成部分）。',
        'share-alike': '相同方式共享 (ShareAlike)：如果您再混合、转换或基于本作品进行创作，您必须基于与原先许可协议相同的许可协议分发您贡献的作品。',
        'commercial-license': '商业授权：',
        'commercial-license-content': '如果您计划将本项目用于商业用途，必须获得作者的额外书面授权。请联系：qinggetech@163.com',
        'contributing-title': '贡献',
        'contributing-content': '如果你喜欢这个项目，欢迎通过 Star 、提交 Issue 和 Pull Request 来帮助改进 OneDroid！',
        'contact-title': '联系我们',
        'email': '邮箱',
        'wechat': '微信'
    },
    en: {
        features: 'Features',
        download: 'Download',
        screenshots: 'Screenshots',
        disclaimer: 'Disclaimer',
        license: 'License',
        contributing: 'Contributing',
        contact: 'Contact Us',
        'hero-title': 'OneDroid - System-level Android All-in-one Toolbox',
        'hero-subtitle': 'A powerful Android multi-functional toolkit that integrates various practical tools to help users better manage and understand their Android devices.',
        'hero-btn': 'Download Now',
        'features-title': 'Features',
        'app-management': 'App Management',
        'app-list': 'App List',
        'app-info': 'Basic Info, Signature, Permissions, Components',
        'app-operations': 'App Operations (Launch, Extract, Uninstall, Modify Internal Files, Decompile)',
        'device-management': 'Device Management',
        'device-info': 'Device, System, CPU, GPU, Memory, Storage, Screen, Network, Location, Sensors, Camera, Temperature, PROP',
        'tool-collection': 'Tool Collection',
        'screen-analysis': 'Screen Analysis: Long Screenshot (In Progress), Layout Inspector, Screen Recording, Color Picker, Text Recognition',
        'reverse-debugging': 'Reverse Debugging: Decompile, Logcat (To be Implemented), Terminal (To be Implemented), Mock Location (To be Implemented), WiFi Password (Requires Root)',
        'network-management': 'Network Management: Packet Capture (To be Implemented), File Server (To be Implemented)',
        'download-title': 'Download',
        'download-desc': 'Download the latest APK from the Release page',
        'download-btn': 'Go to Download',
        'screenshots-title': 'Screenshots',
        'app-management-screenshots': 'App Management',
        'basic-info': 'Basic Info',
        'device-management-screenshots': 'Device Management & Tool Collection',
        'disclaimer-title': 'Disclaimer',
        'disclaimer-content': 'This tool is for learning and technical communication purposes only. Please do not use it for any illegal purposes. Any legal consequences arising from the use of this software are solely the responsibility of the user.',
        'user-agreement': 'User Agreement',
        'privacy-policy': 'Privacy Policy',
        'license-title': 'License',
        'license-content': 'This project is licensed under the CC BY-NC-SA 4.0 (Attribution-NonCommercial-ShareAlike 4.0 International) license.',
        'core-terms': 'Core Terms:',
        'attribution': 'Attribution: You must give appropriate credit, provide a link to the license, and indicate if changes were made.',
        'non-commercial': 'Non-Commercial: You may not use the material for commercial purposes (including but not limited to: direct sale, adding advertisements in the app, using it for paid courses, or as part of a paid service).',
        'share-alike': 'ShareAlike: If you remix, transform, or build upon the material, you must distribute your contributions under the same license as the original.',
        'commercial-license': 'Commercial License:',
        'commercial-license-content': 'If you plan to use this project for commercial purposes, you must obtain additional written authorization from the author. Please contact: qinggetech@163.com',
        'contributing-title': 'Contributing',
        'contributing-content': 'If you like this project, welcome to help improve OneDroid by Star, submitting Issues and Pull Requests!',
        'contact-title': 'Contact Us',
        'email': 'Email',
        'wechat': 'WeChat'
    }
};

// 当前语言
let currentLang = 'zh';

// 语言切换函数
function changeLanguage(lang) {
    currentLang = lang;
    
    // 更新所有带有data-lang-key属性的元素
    document.querySelectorAll('[data-lang-key]').forEach(element => {
        const key = element.getAttribute('data-lang-key');
        if (translations[lang][key]) {
            element.textContent = translations[lang][key];
        }
    });
    
    // 更新语言切换按钮文本
    const langToggle = document.getElementById('langToggle');
    langToggle.textContent = lang === 'zh' ? 'English' : '中文';
    
    // 更新HTML lang属性
    document.documentElement.lang = lang === 'zh' ? 'zh-CN' : 'en';
    
    // 更新页面标题
    document.title = translations[lang]['hero-title'];
}

// 初始化语言切换
function initLanguageToggle() {
    const langToggle = document.getElementById('langToggle');
    langToggle.addEventListener('click', () => {
        const newLang = currentLang === 'zh' ? 'en' : 'zh';
        changeLanguage(newLang);
    });
}

// 滚动动画观察器
function initScrollAnimations() {
    const observerOptions = {
        root: null,
        rootMargin: '0px',
        threshold: 0.1
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    // 为需要动画的元素添加观察
    const animatedElements = document.querySelectorAll(
        '.feature-card, .screenshot-item, .contact-item, .license h3, .license ul, .license p'
    );

    animatedElements.forEach((el, index) => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(30px)';
        el.style.transition = `opacity 0.6s ease ${index * 0.1}s, transform 0.6s ease ${index * 0.1}s`;
        observer.observe(el);
    });
}

// 导航栏滚动效果
function initNavbarScroll() {
    const navbar = document.querySelector('.navbar');
    let lastScroll = 0;

    window.addEventListener('scroll', () => {
        const currentScroll = window.pageYOffset;
        
        if (currentScroll > 100) {
            navbar.style.boxShadow = '0 4px 20px rgba(0, 0, 0, 0.1)';
        } else {
            navbar.style.boxShadow = '0 2px 4px rgba(0, 0, 0, 0.05)';
        }

        lastScroll = currentScroll;
    });
}

// 平滑滚动到锚点
function initSmoothScroll() {
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                const offsetTop = target.offsetTop - 80; // 减去导航栏高度
                window.scrollTo({
                    top: offsetTop,
                    behavior: 'smooth'
                });
            }
        });
    });
}

// 页面加载完成后初始化
window.addEventListener('DOMContentLoaded', () => {
    initLanguageToggle();
    initScrollAnimations();
    initNavbarScroll();
    initSmoothScroll();
    // 初始加载中文
    changeLanguage('zh');
});
