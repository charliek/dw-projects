ruleset {
    description 'Common CodeNarc Rules'

    ruleset('rulesets/basic.xml')
    ruleset('rulesets/braces.xml')
    ruleset('rulesets/concurrency.xml')
    ruleset('rulesets/convention.xml') {
        'CouldBeElvis' enabled: false
    }
    ruleset('rulesets/design.xml') {
        'AbstractClassWithoutAbstractMethod' enabled: false
        'AbstractClassWithPublicConstructor' enabled: false
    }
    ruleset('rulesets/dry.xml') {
        'DuplicateNumberLiteral' enabled: false
        'DuplicateStringLiteral' enabled: false
        'DuplicateListLiteral' enabled: false
    }
    ruleset('rulesets/exceptions.xml')
    ruleset('rulesets/formatting.xml')  {
        'SpaceBeforeOpeningBrace' enabled: false
        'SpaceAfterOpeningBrace' enabled: false
        'SpaceBeforeClosingBrace' enabled: false
        'SpaceAfterClosingBrace' enabled: false
    }
    ruleset('rulesets/generic.xml')
    ruleset('rulesets/groovyism.xml')
    ruleset('rulesets/imports.xml') {
        'MisorderedStaticImports' enabled: false
    }
    ruleset('rulesets/jdbc.xml')
    ruleset('rulesets/junit.xml')
    ruleset('rulesets/logging.xml')
    ruleset('rulesets/naming.xml') {
        'FactoryMethodName' enabled: false
        'FieldName' enabled: false
    }
    ruleset('rulesets/security.xml') {
        'JavaIoPackageAccess' enabled: false
    }
    ruleset('rulesets/size.xml') {
        'CrapMetric' enabled: false
        //Needed to workaround bug in Codenarc 0.18 (already fixed)
        'AbcMetric' description: 'Checks the ABC size metric for methods/classes'
    }
    ruleset('rulesets/unnecessary.xml') {

        // disabling due to code narc bug => http://sourceforge.net/tracker/?func=detail&atid=1126573&aid=3524882&group_id=250145
        'UnnecessaryPackageReference' enabled: false

        'UnnecessaryReturnKeyword' enabled: false
        'UnnecessaryPublicModifier' enabled: false
        'UnnecessarySubstring' enabled: false
    }
    ruleset('rulesets/unused.xml') {
        'UnusedMethodParameter'  enabled: false
        'UnusedPrivateField' {
            enabled = true
            ignoreFieldNames = 'lastUpdated, dateCreated, serialVersionUID'
        }
    }
}
