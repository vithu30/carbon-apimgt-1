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
import TextField from '@material-ui/core/TextField';
import React from 'react';
import Typography from '@material-ui/core/Typography';
import { FormattedMessage, injectIntl } from 'react-intl';
import TagsInput from 'react-tagsinput';
import Button from '@material-ui/core/Button';
import PropTypes from 'prop-types';
import { withRouter } from 'react-router';
import { Link } from 'react-router-dom';
import { withStyles } from '@material-ui/core/styles';
import FormControl from '@material-ui/core/FormControl';
import FormLabel from '@material-ui/core/FormLabel';
import Grid from '@material-ui/core/Grid';
import classNames from 'classnames';
import Alert from 'AppComponents/Shared/Alert';
import Api from 'AppData/api';
import { withAPI } from 'AppComponents/Apis/Details/components/ApiContext';

const styles = theme => ({
    root: {
        flexGrow: 1,
        marginTop: 10,
    },
    titleWrapper: {
        display: 'flex',
        flexDirection: 'row',
        alignItems: 'center',
    },
    contentWrapper: {
        maxWidth: theme.custom.contentAreaWidth,
    },
    mainTitle: {
        paddingLeft: 0,
    },
    FormControl: {
        padding: `0 0 0 ${theme.spacing.unit}px`,
        width: '100%',
        marginTop: 0,
    },
    FormControlOdd: {
        padding: `0 0 0 ${theme.spacing.unit}px`,
        backgroundColor: theme.palette.background.paper,
        width: '100%',
        marginTop: 0,
    },
    FormControlLabel: {
        marginBottom: theme.spacing.unit,
        marginTop: theme.spacing.unit,
        fontSize: theme.typography.caption.fontSize,
    },
    buttonSection: {
        paddingTop: theme.spacing.unit * 2,
    },
    saveButton: {
        marginRight: theme.spacing.unit * 2,
    },
    helpText: {
        color: theme.palette.text.hint,
        marginTop: theme.spacing.unit,
    },
    extraPadding: {
        paddingLeft: theme.spacing.unit * 2,
    },
});

/**
 * Create new scopes for an API
 * @class CreateScope
 * @extends {Component}
 */
class CreateScope extends React.Component {
    constructor(props) {
        super(props);
        this.api = new Api();
        this.api_uuid = props.match.params.api_uuid;
        const valid = [];
        valid.name = {
            invalid: false,
            error: '',
        };
        valid.description = {
            invalid: false,
            error: '',
        };
        this.state = {
            apiScopes: null,
            apiScope: {},
            roles: [],
            valid,
        };
        this.addScope = this.addScope.bind(this);
        this.handleInputs = this.handleInputs.bind(this);
        this.validateScopeName = this.validateScopeName.bind(this);
        this.handleScopeNameInput = this.handleScopeNameInput.bind(this);
        this.validateScopeDescription = this.validateScopeDescription.bind(this);
    }

    /**
     * Add new scope
     * @memberof Scopes
     */
    addScope() {
        const {
            intl, api, history, updateAPI,
        } = this.props;
        if (this.validateScopeName('name', this.state.apiScope.name)) {
            // return status of the validation
            return;
        }
        const scope = this.state.apiScope;

        scope.bindings = {
            type: 'role',
            values: this.state.roles,
        };
        // temp fix to deep copy
        // eslint-disable-next-line no-underscore-dangle
        const newApi = JSON.parse(JSON.stringify(api._data));
        newApi.scopes.push(scope);
        const promisedApiUpdate = updateAPI(newApi);
        promisedApiUpdate.then(() => {
            Alert.info(intl.formatMessage({
                id: 'Apis.Details.Scopes.CreateScope.scope.added.successfully',
                defaultMessage: 'Scope added successfully',
            }));
            const { apiScopes } = this.state;
            const redirectURL = '/apis/' + api.id + '/scopes/';
            history.push(redirectURL);
            this.setState({
                apiScopes,
                apiScope: {},
                roles: [],
            });
        });
        promisedApiUpdate.catch((error) => {
            const { response } = error;
            if (response.body) {
                const { description } = response.body;
                Alert.error(description);
            }
        });
    }

    /**
     * Handle api scope addition event
     * @param {any} event Button Click event
     * @memberof Scopes
     */
    handleInputs(event) {
        if (Array.isArray(event)) {
            this.setState({
                roles: event,
            });
        }
    }

    handleScopeNameInput({ target: { id, value } }) {
        this.validateScopeName(id, value);
    }

    validateScopeName(id, value) {
        const { valid, apiScope } = this.state;
        const {
            api: { scopes },
        } = this.props;

        apiScope[id] = value;
        valid[id].invalid = !(value && value.length > 0);
        if (valid[id].invalid) {
            valid[id].error = 'Scope name cannot be empty';
        }
        const exist = scopes.find((scope) => {
            return scope.name === value;
        });
        if (!valid[id].invalid && exist) {
            valid[id].invalid = true;
            valid[id].error = 'Scope name already exist';
        }
        if (!valid[id].invalid && /[!@#$%^&*(),.?":{}[\]|<>\t\n]/i.test(value)) {
            valid[id].invalid = true;
            valid[id].error = 'Field contains special characters';
        }
        if (!valid[id].invalid) {
            valid[id].error = '';
        }
        this.setState({
            valid,
            apiScope,
        });
        return valid[id].invalid;
    }

    validateScopeDescription({ target: { id, value } }) {
        const { valid, apiScope } = this.state;
        apiScope[id] = value;
        valid[id].invalid = false;
        valid[id].error = '';
        this.setState({
            valid,
            apiScope,
        });
    }

    /**
     *
     *
     * @returns
     * @memberof CreateScope
     */
    render() {
        const { classes } = this.props;
        const url = `/apis/${this.props.api.id}/scopes`;
        return (
            <div className={classes.root}>
                <div className={classes.titleWrapper}>
                    <Typography variant='h4' align='left' className={classes.mainTitle}>
                        <FormattedMessage
                            id='Apis.Details.Scopes.CreateScope.create.new.scope'
                            defaultMessage='Create New Scope'
                        />
                    </Typography>
                </div>
                <div className={classes.contentWrapper}>
                    <FormControl margin='normal' className={classes.FormControl}>
                        <TextField
                            id='name'
                            label='Name'
                            style={{ margin: 8 }}
                            placeholder='Scope Name'
                            error={this.state.valid.name.invalid}
                            helperText={
                                this.state.valid.name.invalid ? (
                                    this.state.valid.name.error
                                ) : (
                                    <FormattedMessage
                                        id='Apis.Details.Scopes.CreateScope.short.description.name'
                                        defaultMessage='Enter Scope Name ( Ex: creator )'
                                    />
                                )
                            }
                            fullWidth
                            margin='normal'
                            InputLabelProps={{
                                shrink: true,
                            }}
                            value={this.state.apiScope.name || ''}
                            onChange={this.handleScopeNameInput}
                        />
                    </FormControl>
                    <FormControl margin='normal' className={classes.FormControlOdd}>
                        <TextField
                            id='description'
                            label='Description'
                            style={{ margin: 8 }}
                            placeholder='Short description about the scope'
                            helperText={
                                <FormattedMessage
                                    id='Apis.Details.Scopes.CreateScope.short.description.about.the.scope'
                                    defaultMessage='Short description about the scope'
                                />
                            }
                            margin='normal'
                            InputLabelProps={{
                                shrink: true,
                            }}
                            onChange={this.validateScopeDescription}
                            value={this.state.apiScope.description || ''}
                            multiline
                        />
                    </FormControl>
                    <FormControl
                        margin='normal'
                        className={classNames({ [classes.FormControl]: true, [classes.extraPadding]: true })}
                    >
                        <FormLabel component='legend' className={classes.FormControlLabel}>
                            <FormattedMessage id='Apis.Details.Scopes.CreateScope.roles' defaultMessage='Roles' />
                        </FormLabel>
                        <TagsInput value={this.state.roles} onChange={this.handleInputs} onlyUnique />
                        <Typography variant='caption' className={classes.helpText}>
                            <FormattedMessage
                                id='Apis.Details.Scopes.CreateScope.roles.help'
                                defaultMessage={
                                    'Enter a valid role and press enter. ' +
                                    'You can do this multiple times to add multiple roles.'
                                }
                            />
                        </Typography>
                    </FormControl>
                    <Grid
                        container
                        direction='row'
                        alignItems='flex-start'
                        spacing={4}
                        className={classes.buttonSection}
                    >
                        <Grid item>
                            <Button
                                variant='contained'
                                color='primary'
                                onClick={this.addScope}
                                disabled={this.state.valid.name.invalid}
                                className={classes.saveButton}
                            >
                                <FormattedMessage id='Apis.Details.Scopes.CreateScope.save' defaultMessage='Save' />
                            </Button>
                            <Link to={url}>
                                <Button variant='contained'>
                                    <FormattedMessage
                                        id='Apis.Details.Scopes.CreateScope.cancel'
                                        defaultMessage='Cancel'
                                    />
                                </Button>
                            </Link>
                        </Grid>
                    </Grid>
                </div>
            </div>
        );
    }
}

CreateScope.propTypes = {
    match: PropTypes.shape({
        params: PropTypes.object,
    }),
    api: PropTypes.shape({
        id: PropTypes.string,
    }).isRequired,
    history: PropTypes.shape({ push: PropTypes.func }).isRequired,
    classes: PropTypes.shape({}).isRequired,
    intl: PropTypes.shape({ formatMessage: PropTypes.func }).isRequired,
    updateAPI: PropTypes.func.isRequired,
};

CreateScope.defaultProps = {
    match: { params: {} },
};

export default withAPI(injectIntl(withRouter(withStyles(styles)(CreateScope))));
