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

import React, { Fragment, useState } from 'react';
import PropTypes from 'prop-types';
import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import Grid from '@material-ui/core/Grid';
import Box from '@material-ui/core/Box';
import Tooltip from '@material-ui/core/Tooltip';
import Divider from '@material-ui/core/Divider';
import Button from '@material-ui/core/Button';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails';
import Typography from '@material-ui/core/Typography';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import { makeStyles } from '@material-ui/core/styles';
import Utils from 'AppData/Utils';
import DeleteIcon from '@material-ui/icons/Delete';
import IconButton from '@material-ui/core/IconButton';
import Badge from '@material-ui/core/Badge';
import ReportProblemOutlinedIcon from '@material-ui/icons/ReportProblemOutlined';
// splitted operation components

import DescriptionAndSummary from './operationComponents/DescriptionAndSummary';
import OperationGovernance from './operationComponents/OperationGovernance';
import Parameters from './operationComponents/Parameters';

/**
 *
 * Handle the operation UI
 * @export
 * @param {*} props
 * @returns {React.Component} @inheritdoc
 */
function Operation(props) {
    const {
        operation,
        operationsDispatcher,
        highlight,
        operationRateLimits,
        api,
        disableDelete,
        disableUpdate,
        onMarkAsDelete,
        markAsDelete,
        hideParameters,
        spec,
        target,
        verb,
    } = props;
    const [isExpanded, setIsExpanded] = useState(false);
    const useStyles = makeStyles((theme) => {
        const backgroundColor = theme.custom.resourceChipColors[verb];
        return {
            customButton: {
                '&:hover': { backgroundColor },
                backgroundColor,
                width: theme.spacing(12),
            },
            paperStyles: {
                border: `1px solid ${backgroundColor}`,
                borderBottom: '',
            },
            customDivider: {
                backgroundColor,
            },
            linearProgress: {
                height: '2px',
            },
            highlightSelected: {
                backgroundColor: Utils.hexToRGBA(backgroundColor, 0.1),
            },
            contentNoMargin: {
                margin: theme.spacing(0),
            },
            overlayUnmarkDelete: {
                position: 'absolute',
                zIndex: theme.zIndex.operationDeleteUndo,
                right: '10%',
            },
        };
    });
    const apiOperation = api.operations[target] && api.operations[target][verb.toUpperCase()];
    const isUsedInAPIProduct =
        apiOperation && Array.isArray(apiOperation.usedProductIds) && apiOperation.usedProductIds.length;

    /**
     *
     *
     * @param {*} event
     */
    function toggleDelete(event) {
        event.stopPropagation();
        event.preventDefault();
        setIsExpanded(false);
        onMarkAsDelete({ verb, target }, !markAsDelete);
    }

    /**
     *
     *
     * @param {*} event
     * @param {*} expanded
     */
    function handleExpansion(event, expanded) {
        setIsExpanded(expanded);
    }
    const classes = useStyles();
    return (
        <Fragment>
            {markAsDelete && (
                <Box className={classes.overlayUnmarkDelete}>
                    <Tooltip title='Marked for delete' aria-label='Marked for delete'>
                        <Button onClick={toggleDelete} variant='outlined' style={{ marginTop: '10px' }}>
                            Undo
                        </Button>
                    </Tooltip>
                </Box>
            )}
            <ExpansionPanel
                expanded={isExpanded}
                onChange={handleExpansion}
                disabled={markAsDelete}
                className={classes.paperStyles}
            >
                <ExpansionPanelSummary
                    className={highlight ? classes.highlightSelected : ''}
                    disableRipple
                    disableTouchRipple
                    expandIcon={<ExpandMoreIcon />}
                    aria-controls='panel2a-content'
                    id='panel2a-header'
                    classes={{ content: classes.contentNoMargin }}
                >
                    <Grid container direction='row' justify='space-between' alignItems='center' spacing={0}>
                        <Grid item md={6}>
                            <Badge invisible={!operation['x-wso2-new']} color='error' variant='dot'>
                                <Button
                                    disableFocusRipple
                                    variant='contained'
                                    size='small'
                                    className={classes.customButton}
                                >
                                    {verb}
                                </Button>
                            </Badge>
                            <Typography display='inline' style={{ margin: '0px 30px' }} variant='h6' gutterBottom>
                                {target}

                                <Typography
                                    display='inline'
                                    style={{ margin: '0px 30px' }}
                                    variant='caption'
                                    gutterBottom
                                >
                                    {operation.summary}
                                </Typography>
                            </Typography>
                        </Grid>
                        {Boolean(isUsedInAPIProduct) && (
                            <Grid item md={4}>
                                <Box display='flex'>
                                    <ReportProblemOutlinedIcon fontSize='small' />
                                    <Box display='flex' ml={1} mt={1 / 4} fontSize='caption.fontSize'>
                                        This operation is used in {isUsedInAPIProduct} API product(s)
                                    </Box>
                                </Box>
                            </Grid>
                        )}
                        {!(disableDelete || markAsDelete) && (
                            <Grid item md={1}>
                                <Tooltip
                                    title={
                                        isUsedInAPIProduct
                                            ? "Can't delete operation when used in an API product"
                                            : 'Delete'
                                    }
                                    aria-label='Delete operation'
                                >
                                    <div>
                                        <IconButton
                                            disabled={Boolean(isUsedInAPIProduct) || disableUpdate}
                                            onClick={toggleDelete}
                                            aria-label='delete'
                                        >
                                            <DeleteIcon fontSize='small' />
                                        </IconButton>
                                    </div>
                                </Tooltip>
                            </Grid>
                        )}
                    </Grid>
                </ExpansionPanelSummary>
                <Divider light className={classes.customDivider} />
                <ExpansionPanelDetails>
                    <Grid spacing={2} container direction='row' justify='flex-start' alignItems='flex-start'>
                        <DescriptionAndSummary
                            operation={operation}
                            operationsDispatcher={operationsDispatcher}
                            disableUpdate={disableUpdate}
                            target={target}
                            verb={verb}
                        />
                        <OperationGovernance
                            operation={operation}
                            operationsDispatcher={operationsDispatcher}
                            operationRateLimits={operationRateLimits}
                            api={api}
                            disableUpdate={disableUpdate}
                            spec={spec}
                            target={target}
                            verb={verb}
                        />
                        {!hideParameters && (
                            <Parameters
                                operation={operation}
                                operationsDispatcher={operationsDispatcher}
                                operationRateLimits={operationRateLimits}
                                api={api}
                                disableUpdate={disableUpdate}
                                spec={spec}
                                target={target}
                                verb={verb}
                            />
                        )}
                    </Grid>
                </ExpansionPanelDetails>
            </ExpansionPanel>
        </Fragment>
    );
}
Operation.defaultProps = {
    highlight: false,
    disableUpdate: false,
    /* Set following prop to false , After implementing the `Parameter` section */
    hideParameters: false,
    disableDelete: false,
    onMarkAsDelete: () => {},
    markAsDelete: false,
    operationRateLimits: [], // Response body.list from apis policies for `api` throttling policies type
};
Operation.propTypes = {
    api: PropTypes.shape({ scopes: PropTypes.arrayOf(PropTypes.shape({})) }).isRequired,
    operationsDispatcher: PropTypes.func.isRequired,
    onMarkAsDelete: PropTypes.func,
    markAsDelete: PropTypes.bool,
    disableDelete: PropTypes.bool,
    disableUpdate: PropTypes.bool,
    hideParameters: PropTypes.bool,
    operation: PropTypes.shape({}).isRequired,
    target: PropTypes.string.isRequired,
    verb: PropTypes.string.isRequired,
    spec: PropTypes.shape({}).isRequired,
    highlight: PropTypes.bool,
    operationRateLimits: PropTypes.arrayOf(PropTypes.shape({})),
};

export default React.memo(Operation);
