/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React from 'react';

import { withStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import InputLabel from '@material-ui/core/InputLabel';
import FormControl from '@material-ui/core/FormControl';
import FormHelperText from '@material-ui/core/FormHelperText';
import Checkbox from '@material-ui/core/Checkbox';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import { FormattedMessage, injectIntl } from 'react-intl';
import Settings from 'Settings';
import PropTypes from 'prop-types';
import ResourceNotFound from '../../Base/Errors/ResourceNotFound';

const styles = theme => ({
    FormControl: {
        padding: theme.spacing.unit * 2,
        width: '100%',
    },
    FormControlOdd: {
        padding: theme.spacing.unit * 2,
        width: '100%',
    },
    button: {
        marginLeft: theme.spacing.unit * 1,
    },
    quotaHelp: {
        position: 'relative',
    },
    checkboxWrapper: {
        display: 'flex',
    },
    checkboxWrapperColumn: {
        display: 'flex',
        flexDirection: 'row',
    },
    group: {
        flexDirection: 'row',
    },
});
/**
 *
 *
 * @class KeyConfiguration
 * @extends {React.Component}
 */
class KeyConfiguration extends React.Component {
    /**
     * Get the display names for the server supported grant types
     * @param serverSupportedGrantTypes
     * @param grantTypeDisplayNameMap
     */
    getGrantTypeDisplayList(serverSupportedGrantTypes, grantTypeDisplayNameMap) {
        const modifiedserverSupportedGrantTypes = {};
        serverSupportedGrantTypes.forEach((grantType) => {
            modifiedserverSupportedGrantTypes[grantType] = grantTypeDisplayNameMap[grantType];
            if (!grantTypeDisplayNameMap[grantType]) {
                modifiedserverSupportedGrantTypes[grantType] = grantType;
            }
        });
        return modifiedserverSupportedGrantTypes;
    }

    /**
     * This method is used to handle the updating of key generation
     * request object.
     * @param {*} field field that should be updated in key request
     * @param {*} event event fired
     */
    handleChange(field, event) {
        const { keyRequest, updateKeyRequest } = this.props;
        const newRequest = { ...keyRequest };
        const { target: currentTarget } = event;
        let newGrantTypes = [...newRequest.supportedGrantTypes];

        switch (field) {
            case 'callbackUrl':
                newRequest.callbackUrl = currentTarget.value;
                break;
            case 'grantType':
                if (currentTarget.checked) {
                    newGrantTypes = [...newGrantTypes, currentTarget.id];
                } else {
                    newGrantTypes = newRequest.supportedGrantTypes.filter(item => item !== currentTarget.id);
                }
                newRequest.supportedGrantTypes = newGrantTypes;
                break;
            default:
                break;
        }
        updateKeyRequest(newRequest);
    }

    /**
     * returns whether grant type checkbox should be disabled or not
     * @param grantType
     */
    isGrantTypeDisabled(grantType) {
        const { keyRequest, isUserOwner } = this.props;
        const { callbackUrl } = keyRequest;
        return !(isUserOwner && !(!callbackUrl && (grantType === 'authorization_code' || grantType === 'implicit')));
    }

    /**
     *
     *
     * @returns {Component}
     * @memberof KeyConfiguration
     */
    render() {
        const {
            classes, keyRequest, notFound, intl, isUserOwner,
        } = this.props;
        const { serverSupportedGrantTypes, supportedGrantTypes, callbackUrl } = keyRequest;
        if (notFound) {
            return <ResourceNotFound />;
        }
        const grantTypeDisplayListMap = this.getGrantTypeDisplayList(
            serverSupportedGrantTypes,
            Settings.grantTypes,
        );

        return (
            <React.Fragment>
                <FormControl className={classes.FormControl} component='fieldset'>
                    <InputLabel shrink htmlFor='age-label-placeholder' className={classes.quotaHelp}>
                        <FormattedMessage id='grant.types' defaultMessage='Grant Types' />
                    </InputLabel>
                    <div className={classes.checkboxWrapper}>
                        <div className={classes.checkboxWrapperColumn}>
                            {Object.keys(grantTypeDisplayListMap).map((key) => {
                                const value = grantTypeDisplayListMap[key];
                                return (
                                    <FormControlLabel
                                        control={(
                                            <Checkbox
                                                id={key}
                                                checked={!!(supportedGrantTypes
                                                    && supportedGrantTypes.includes(key))
                                                    && !this.isGrantTypeDisabled(key)}
                                                onChange={e => this.handleChange('grantType', e)}
                                                value={value}
                                                disabled={this.isGrantTypeDisabled(key)}
                                                color='primary'
                                            />
                                        )}
                                        label={intl.formatMessage({
                                            defaultMessage: value,
                                            id: 'Shared.AppsAndKeys.KeyConfiguration.' + value.replace(/ /g, '.'),
                                        })}
                                    />
                                );
                            })}
                        </div>
                    </div>
                    <FormHelperText>
                        <FormattedMessage
                            defaultMessage={`The application can use the following grant types to generate 
                            Access Tokens. Based on the application requirement,you can enable or disable 
                            grant types for this application.`}
                            id='Shared.AppsAndKeys.KeyConfiguration.the.application.can'
                        />
                    </FormHelperText>
                </FormControl>

                {
                    <FormControl className={classes.FormControlOdd}>
                        <TextField
                            id='callbackURL'
                            fullWidth
                            onChange={e => this.handleChange('callbackUrl', e)}
                            label='Callback URL'
                            placeholder='http://url-to-webapp'
                            className={classes.textField}
                            margin='normal'
                            value={callbackUrl}
                            disabled={!isUserOwner}
                        />
                        <FormHelperText>
                            <FormattedMessage
                                defaultMessage={`Callback URL is a redirection URI in the client
                                application which is used by the authorization server to send the
                                client's user-agent (usually web browser) back after granting access.`}
                                id='Shared.AppsAndKeys.KeyConfiguration.callback.url'
                            />
                        </FormHelperText>
                    </FormControl>
                }
            </React.Fragment>
        );
    }
}

KeyConfiguration.propTypes = {
    classes: PropTypes.instanceOf(Object).isRequired,
    keyRequest: PropTypes.shape({
        callbackUrl: PropTypes.string,
        serverSupportedGrantTypes: PropTypes.array,
        supportedGrantTypes: PropTypes.array,
    }).isRequired,
    isUserOwner: PropTypes.bool.isRequired,
    notFound: PropTypes.bool.isRequired,
    updateKeyRequest: PropTypes.func.isRequired,
    intl: PropTypes.shape({ formatMessage: PropTypes.func }).isRequired,
};


export default injectIntl(withStyles(styles)(KeyConfiguration));
